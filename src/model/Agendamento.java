package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Agendamento implements Serializable {
    private int id;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String status; // MARCADO, CANCELADO, CONCLUIDO
    private int idCliente;
    private int idProfissional;
    private int idServico;

    public Agendamento() {}

    public Agendamento(int id, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String status, int idCliente, int idProfissional, int idServico) {
        this.id = id;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = status;
        this.idCliente = idCliente;
        this.idProfissional = idProfissional;
        this.idServico = idServico;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }
    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }
}
