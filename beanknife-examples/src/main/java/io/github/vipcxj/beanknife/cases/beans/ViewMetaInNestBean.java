package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

@ViewMeta
public class ViewMetaInNestBean {

    private int a;
    private String b;

    public int getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @ViewMeta
    static class Bean1 {

        private int a;
        private String b;

        public int getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public static class Bean2 {

            private int a;
            private String b;

            public int getA() {
                return a;
            }

            public String getB() {
                return b;
            }

            public void setB(String b) {
                this.b = b;
            }
            @ViewMeta
            protected static class Bean3 {

                private int a;
                private String b;

                public int getA() {
                    return a;
                }

                public String getB() {
                    return b;
                }

                public void setB(String b) {
                    this.b = b;
                }
            }
        }
    }

    static class Bean2 {
        int a;
        @ViewMeta
        class Bean1 {
            long b;
            @ViewMeta
            class Bean3 {
                String c;
            }
        }
    }
}
