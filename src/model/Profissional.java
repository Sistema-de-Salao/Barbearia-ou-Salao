package model;

import java.io.Serializable;

public class Profissional implements Serializable {
    private int id;
    private String nome;
    private String especialidade;
    private boolean ativo;

    public Profissional() {}

    public Profissional(int id, String nome, String especialidade, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.ativo = ativo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome;
    }
}
