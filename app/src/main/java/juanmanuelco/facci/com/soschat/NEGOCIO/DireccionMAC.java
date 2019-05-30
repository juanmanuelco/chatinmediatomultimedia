package juanmanuelco.facci.com.soschat.NEGOCIO;

public class DireccionMAC {
    public static String direccion="";
    public static String nombre=Dispositivo.getDeviceName();
    public static String wifiNombre="";
    public static String MacOnclick="";

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    String mac;
}
