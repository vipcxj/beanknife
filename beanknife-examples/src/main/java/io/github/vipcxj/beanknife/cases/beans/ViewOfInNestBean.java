package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewOf;

public class ViewOfInNestBean {

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

    @ViewOf
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
            @ViewOf
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
        @ViewOf
        class Bean1 {
            long b;
            @ViewOf
            class Bean3 {
                String c;
            }
        }
    }
}
