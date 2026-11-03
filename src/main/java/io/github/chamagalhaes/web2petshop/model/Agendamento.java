package io.github.chamagalhaes.web2petshop.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agendamentos")
@Getter @Setter
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data e hora são obrigatórias")
    @FutureOrPresent(message = "A data não pode estar no passado")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @NotBlank(message = "O status é obrigatório")
    @Column(nullable = false)
    private String status;

    @Valid
    @NotEmpty(message = "Adicione pelo menos um serviço")
    @OneToMany(
            mappedBy = "agendamento",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AgendamentoServico> servicos = new ArrayList<>();

    @NotNull(message = "É obrigatório selecionar um pet")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public void addServico(AgendamentoServico servico) {
        servicos.add(servico);
        servico.setAgendamento(this);
    }

    public void calculateTotal() {
        this.total = servicos.stream()
                .map(AgendamentoServico::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
