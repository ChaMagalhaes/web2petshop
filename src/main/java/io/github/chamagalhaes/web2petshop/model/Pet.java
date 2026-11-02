package io.github.chamagalhaes.web2petshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 3, max = 80, message = "Nome deve conter pelo menos 3 caracteres")
    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Size(min = 3, max = 80, message = "Raca deve conter pelo menos 3 caracteres")
    @Column(name = "raca", nullable = false)
    private String raca;

    @NotNull(message = "Informe a idade válida")
    @Column(name = "idade", nullable = false)
    private int idade;

    @NotBlank(message = "Tamanho é um campo obrigatório")
    @Pattern(
            regexp = "PEQUENO|MEDIO|GRANDE",
            message = "Selecione um tamanho válido: PEQUENO, MEDIO ou GRANDE"
    )
    @Column(name = "tamanho", nullable = false)
    private String tamanho;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }
}
