package model;

public class SessaoUsuario {
    private static SessaoUsuario instance;
    private String usuario;
    private NivelAcesso nivelAcesso;
    private int idVinculado; // ID do Cliente ou Profissional, se aplicável

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public void login(String usuario, NivelAcesso nivelAcesso, int idVinculado) {
        this.usuario = usuario;
        this.nivelAcesso = nivelAcesso;
        this.idVinculado = idVinculado;
    }

    public void logout() {
        this.usuario = null;
        this.nivelAcesso = null;
        this.idVinculado = -1;
    }

    public String getUsuario() { return usuario; }
    public NivelAcesso getNivelAcesso() { return nivelAcesso; }
    public int getIdVinculado() { return idVinculado; }
}
