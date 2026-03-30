package model;

import java.io.Serializable;
import java.time.LocalTime;

public class HorarioProfissional implements Serializable {
    private int id;
    private int idProfissional;
    private int diaSemana; // 1 (Segunda) a 7 (Domingo)
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public HorarioProfissional() {}

    public HorarioProfissional(int id, int idProfissional, int diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.id = id;
        this.idProfissional = idProfissional;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }
    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
