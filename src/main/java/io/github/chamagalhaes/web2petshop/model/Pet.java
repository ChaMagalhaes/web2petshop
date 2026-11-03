package io.github.chamagalhaes.web2petshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pet")
@Getter @Setter
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 80,
            message = "Nome deve conter entre 3 e 80 caracteres")
    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Size(min = 3, max = 80,
            message = "Raça deve conter entre 3 e 80 caracteres")
    @NotBlank(message = "Raça é um campo obrigatório")
    @Column(name = "raca", nullable = false)
    private String raca;

    @NotNull(message = "Informe a idade")
    @Min(value = 0, message = "A idade não pode ser negativa")
    @Column(name = "idade", nullable = false)
    private Integer idade;

    @NotBlank(message = "Tamanho é um campo obrigatório")
    @Pattern(
            regexp = "^(PEQUENO|MEDIO|GRANDE)$",
            message = "Selecione um tamanho válido: PEQUENO, MEDIO ou GRANDE"
    )
    @Column(name = "tamanho", nullable = false)
    private String tamanho;

    @OneToMany(mappedBy = "pet")
    private List<Agendamento> agendamentos = new ArrayList<>();

    public void addAgendamento(Agendamento agendamento) {
        agendamentos.add(agendamento);
        agendamento.setPet(this);
    }
}
