package dao;

import model.Servico;
import java.util.List;

public class ServicoDAO extends BaseDAO<Servico> {
    public ServicoDAO() {
        super("servicos.txt");
    }

    public void adicionar(Servico servico) {
        List<Servico> servicos = getAll();
        int nextId = servicos.stream().mapToInt(Servico::getId).max().orElse(0) + 1;
        servico.setId(nextId);
        servicos.add(servico);
        saveAll(servicos);
    }

    public void atualizar(Servico servico) {
        List<Servico> servicos = getAll();
        for (int i = 0; i < servicos.size(); i++) {
            if (servicos.get(i).getId() == servico.getId()) {
                servicos.set(i, servico);
                break;
            }
        }
        saveAll(servicos);
    }

    public void remover(int id) {
        List<Servico> servicos = getAll();
        servicos.removeIf(s -> s.getId() == id);
        saveAll(servicos);
    }

    public Servico buscarPorId(int id) {
        return getAll().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }
}
