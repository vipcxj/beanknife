/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.vipcxj.beanknife.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;

/**
 * @author David Blevins
 * @version $Rev$ $Date$
 */
public class ResourceFinder {

    private final URL[] urls;
    private final String path;
    private final ClassLoader classLoader;

    public ResourceFinder(String path, ClassLoader classLoader) {
        this(path, classLoader, (URL[]) null);
    }

    public ResourceFinder(String path, ClassLoader classLoader, URL... urls) {
        if (path == null){
            path = "";
        } else if (path.length() > 0 && !path.endsWith("/")) {
            path += "/";
        }
        this.path = path;

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        this.classLoader = classLoader;

        for (int i = 0; urls != null && i < urls.length; i++) {
            URL url = urls[i];
            if (url == null || isDirectory(url) || url.getProtocol().equals("jar")) {
                continue;
            }
            try {
                urls[i] = new URL("jar", "", -1, url + "!/");
            } catch (MalformedURLException ignored) {
            }
        }
        this.urls = (urls == null || urls.length == 0) ? null : urls;
    }

    private static boolean isDirectory(URL url) {
        String file = url.getFile();
        return (file.length() > 0 && file.charAt(file.length() - 1) == '/');
    }


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find String
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Reads the contents of the found URLs as a list of {@link String}'s and returns them.
     *
     * @param uri uri
     * @return a list of the content of each resource URL found
     * @throws IOException if any of the found URLs are unable to be read.
     */
    public List<String> findAllStrings(String uri) throws IOException {
        String fulluri = path + uri;

        List<String> strings = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String string = readContents(url);
            strings.add(string);
        }
        return strings;
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Implementation
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Assumes the class specified points to a file in the classpath that contains
     * the name of a class that implements or is a subclass of the specfied class.
     * <p/>
     * Any class that cannot be loaded or assigned to the specified interface will be cause
     * an exception to be thrown.
     * <p/>
     * Example classpath:
     * <p/>
     * META-INF/java.io.InputStream    # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.InputStream    # contains the classname org.widget.NeatoInputStream
     * META-INF/java.io.InputStream    # contains the classname com.foo.BarInputStream
     * <p/>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List classes = finder.findAllImplementations(java.io.InputStream.class);
     * classes.contains("org.acme.AcmeInputStream");  // true
     * classes.contains("org.widget.NeatoInputStream");  // true
     * classes.contains("com.foo.BarInputStream");  // true
     *
     * @param interfase a superclass or interface
     * @return implementations
     * @throws IOException            if the URL cannot be read
     * @throws ClassNotFoundException if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or interface
     */
    public <T> List<Class<? extends T>> findAllImplementations(Class<T> interfase) throws IOException, ClassNotFoundException {
        List<Class<? extends T>> implementations = new ArrayList<>();
        List<String> strings = findAllStrings(interfase.getName());
        for (String string : strings) {
            String[] classNames = string.split("[\\r\\n]+");
            for (String className : classNames) {
                className = className.trim();
                if (!className.isEmpty()) {
                    Class<?> impl = classLoader.loadClass(className);
                    if (!interfase.isAssignableFrom(impl)) {
                        throw new ClassCastException("Class not of type: " + interfase.getName());
                    }
                    //noinspection unchecked
                    implementations.add((Class<? extends T>) impl);
                }
            }
        }
        return implementations;
    }

    private String readContents(URL resource) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (InputStream in = resource.openStream(); BufferedInputStream reader = new BufferedInputStream(in)) {

            int b = reader.read();
            while (b != -1) {
                sb.append((char) b);
                b = reader.read();
            }

            return sb.toString().trim();
        }
    }

    private Enumeration<URL> getResources(String fulluri) throws IOException {
        if (urls == null) {
            return classLoader.getResources(fulluri);
        }
        Vector<URL> resources = new Vector<>();
        for (URL url : urls) {
            URL resource = findResource(fulluri, url);
            if (resource != null){
                resources.add(resource);
            }
        }
        return resources.elements();
    }

    private URL findResource(String resourceName, URL... search) {
        for (int i = 0; i < search.length; i++) {
            URL currentUrl = search[i];
            if (currentUrl == null) {
                continue;
            }

            try {
                String protocol = currentUrl.getProtocol();
                if (protocol.equals("jar")) {
                    /*
                     * If the connection for currentUrl or resURL is
                     * used, getJarFile() will throw an exception if the
                     * entry doesn't exist.
                     */
                    URL jarURL = ((JarURLConnection) currentUrl.openConnection()).getJarFileURL();
                    JarFile jarFile;
                    JarURLConnection juc;
                    try {
                        juc = (JarURLConnection) new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                    } catch (IOException e) {
                        // Don't look for this jar file again
                        search[i] = null;
                        throw e;
                    }

                    try {
                        juc = (JarURLConnection) new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                        String entryName;
                        if (currentUrl.getFile().endsWith("!/")) {
                            entryName = resourceName;
                        } else {
                            String file = currentUrl.getFile();
                            int sepIdx = file.lastIndexOf("!/");
                            if (sepIdx == -1) {
                                // Invalid URL, don't look here again
                                search[i] = null;
                                continue;
                            }
                            sepIdx += 2;
                            entryName = file.substring(sepIdx) + resourceName;
                        }
                        if (entryName.equals("META-INF/") && jarFile.getEntry("META-INF/MANIFEST.MF") != null) {
                            return targetURL(currentUrl, "META-INF/MANIFEST.MF");
                        }
                        if (jarFile.getEntry(entryName) != null) {
                            return targetURL(currentUrl, resourceName);
                        }
                    } finally {
                        if (!juc.getUseCaches()) {
                            try {
                                jarFile.close();
                            } catch (Exception ignored) {
                            }
                        }
                    }

                } else if (protocol.equals("file")) {
                    String baseFile = currentUrl.getFile();
                    String host = currentUrl.getHost();
                    int hostLength = 0;
                    if (host != null) {
                        hostLength = host.length();
                    }
                    StringBuilder buf = new StringBuilder(2 + hostLength + baseFile.length() + resourceName.length());

                    if (hostLength > 0) {
                        buf.append("//").append(host);
                    }
                    // baseFile always ends with '/'
                    buf.append(baseFile);
                    String fixedResName = resourceName;
                    // Do not create a UNC path, i.e. \\host
                    while (fixedResName.startsWith("/") || fixedResName.startsWith("\\")) {
                        fixedResName = fixedResName.substring(1);
                    }
                    buf.append(fixedResName);
                    String filename = buf.toString();
                    File file = new File(filename);
                    File file2 = new File(URLDecoder.decode(filename));

                    if (file.exists() || file2.exists()) {
                        return targetURL(currentUrl, fixedResName);
                    }
                } else {
                    URL resourceURL = targetURL(currentUrl, resourceName);
                    URLConnection urlConnection = resourceURL.openConnection();

                    try {
                        urlConnection.getInputStream().close();
                    } catch (SecurityException e) {
                        return null;
                    }
                    // HTTP can return a stream on a non-existent file
                    // So check for the return code;
                    if (!resourceURL.getProtocol().equals("http")) {
                        return resourceURL;
                    }

                    int code = ((HttpURLConnection) urlConnection).getResponseCode();
                    if (code >= 200 && code < 300) {
                        return resourceURL;
                    }
                }
            } catch (MalformedURLException e) {
                // Keep iterating through the URL list
            } catch (IOException | SecurityException ignored) {
            }
        }
        return null;
    }

    private URL targetURL(URL base, String name) throws MalformedURLException {
        String file = base.getFile() + name;
        return new URL(base.getProtocol(), base.getHost(), base.getPort(), file, null);
    }
}