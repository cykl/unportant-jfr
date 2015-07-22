package info.unportant.jfr;

class JreVersion {

    public static boolean isOpenJDK() {
        return System.getProperty("java.vm.name").contains("OpenJDK");
    }

    public static boolean isOracle() {
        return !isOpenJDK();
    }
}
