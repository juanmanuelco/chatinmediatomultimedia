package juanmanuelco.facci.com.soschat.Entities;

public class ENCONTRADO {

    private String Mac_destino;
    private String nickname;
    private Boolean estado;

    /*public ENCONTRADO(String mac_destino, String nickname) {
        this.Mac_destino = mac_destino;
        this.nickname = nickname;
    }*/



    public String getMac_destino() {
        return Mac_destino;
    }

    public void setMac_destino(String mac_destino) {
        this.Mac_destino = mac_destino;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "ENCONTRADO{" +
                "Mac_destino='" + Mac_destino + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
