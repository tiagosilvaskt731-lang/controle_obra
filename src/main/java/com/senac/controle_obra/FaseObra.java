package com.senac.controle_obra;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FaseObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeObra; // Campo para o Nome do Empreendimento
    private String nome;
    private int atividadesTotais;
    private int atividadesExecutadas;
    private int atividadesRefazer;
    private String usuario;

    public FaseObra() {}

    public FaseObra(String nome, int atividadesTotais, int atividadesExecutadas, String usuario, String nomeObra) {
        this.nome = nome;
        this.atividadesTotais = atividadesTotais;
        this.atividadesExecutadas = atividadesExecutadas;
        this.atividadesRefazer = 0;
        this.usuario = usuario;
        this.nomeObra = nomeObra;
    }

    public int getRestante() {
        int restante = atividadesTotais - atividadesExecutadas;
        return restante < 0 ? 0 : restante;
    }

    public double getPorcentagem() {
        if (atividadesTotais == 0) return 0;
        return ((double) atividadesExecutadas / atividadesTotais) * 100;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeObra() { return nomeObra; }
    public void setNomeObra(String nomeObra) { this.nomeObra = nomeObra; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getAtividadesTotais() { return atividadesTotais; }
    public void setAtividadesTotais(int atividadesTotais) { this.atividadesTotais = atividadesTotais; }

    public int getAtividadesExecutadas() { return atividadesExecutadas; }
    public void setAtividadesExecutadas(int atividadesExecutadas) { this.atividadesExecutadas = atividadesExecutadas; }

    public int getAtividadesRefazer() { return atividadesRefazer; }
    public void setAtividadesRefazer(int atividadesRefazer) { this.atividadesRefazer = atividadesRefazer; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}