package dao;

import model.HorarioProfissional;
import java.util.List;
import java.util.stream.Collectors;

public class HorarioProfissionalDAO extends BaseDAO<HorarioProfissional> {
    public HorarioProfissionalDAO() {
        super("horarios_profissionais.txt");
    }

    public void adicionar(HorarioProfissional horario) {
        List<HorarioProfissional> horarios = getAll();
        int nextId = horarios.stream().mapToInt(HorarioProfissional::getId).max().orElse(0) + 1;
        horario.setId(nextId);
        horarios.add(horario);
        saveAll(horarios);
    }

    public void atualizar(HorarioProfissional horario) {
        List<HorarioProfissional> horarios = getAll();
        for (int i = 0; i < horarios.size(); i++) {
            if (horarios.get(i).getId() == horario.getId()) {
                horarios.set(i, horario);
                break;
            }
        }
        saveAll(horarios);
    }

    public void remover(int id) {
        List<HorarioProfissional> horarios = getAll();
        horarios.removeIf(h -> h.getId() == id);
        saveAll(horarios);
    }

    public List<HorarioProfissional> listarPorProfissional(int idProfissional) {
        return getAll().stream()
                .filter(h -> h.getIdProfissional() == idProfissional)
                .collect(Collectors.toList());
    }
}
