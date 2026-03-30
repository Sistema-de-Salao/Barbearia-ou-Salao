package dao;

import model.Agendamento;
import java.util.List;
import java.util.stream.Collectors;

public class AgendamentoDAO extends BaseDAO<Agendamento> {
    public AgendamentoDAO() {
        super("agendamentos.txt");
    }

    public void adicionar(Agendamento agendamento) {
        List<Agendamento> agendamentos = getAll();
        int nextId = agendamentos.stream().mapToInt(Agendamento::getId).max().orElse(0) + 1;
        agendamento.setId(nextId);
        agendamentos.add(agendamento);
        saveAll(agendamentos);
    }

    public void atualizar(Agendamento agendamento) {
        List<Agendamento> agendamentos = getAll();
        for (int i = 0; i < agendamentos.size(); i++) {
            if (agendamentos.get(i).getId() == agendamento.getId()) {
                agendamentos.set(i, agendamento);
                break;
            }
        }
        saveAll(agendamentos);
    }

    public void remover(int id) {
        List<Agendamento> agendamentos = getAll();
        agendamentos.removeIf(a -> a.getId() == id);
        saveAll(agendamentos);
    }

    public Agendamento buscarPorId(int id) {
        return getAll().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public List<Agendamento> listarPorProfissional(int idProfissional) {
        return getAll().stream()
                .filter(a -> a.getIdProfissional() == idProfissional)
                .collect(Collectors.toList());
    }
}
