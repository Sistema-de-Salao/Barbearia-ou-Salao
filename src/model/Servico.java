package model;

import java.io.Serializable;

public class Servico implements Serializable {
    private int id;
    private String nome;
    private int duracaoMinutos;
    private double preco;

    public Servico() {}

    public Servico(int id, String nome, int duracaoMinutos, double preco) {
        this.id = id;
        this.nome = nome;
        this.duracaoMinutos = duracaoMinutos;
        this.preco = preco;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    @Override
    public String toString() {
        return nome + " (" + duracaoMinutos + " min)";
    }
}
