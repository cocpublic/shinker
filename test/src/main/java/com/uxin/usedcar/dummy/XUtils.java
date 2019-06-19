package com.uxin.usedcar.dummy;

public class XUtils {
    public static void main(String[] args) {
//        String className = "com/R$id";
        String className = "com/R";
        System.out.println("isR= " + isRClass(className));
    }

    /**
     * @return true if name matches pattern like {@code .+/R$.+}
     */
    static boolean isRClass(String className) {
        int $ = className.lastIndexOf('$');
        int slash = className.lastIndexOf('/', $);
        System.out.println("i$= " + $ + " ,slash=" + slash);

        return $ > slash && $ < className.length() && (className.charAt(slash + 1) | className.charAt($ - 1)) == 'R';
    }
}
