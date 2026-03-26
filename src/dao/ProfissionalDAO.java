package dao;

import model.Profissional;
import java.util.List;

public class ProfissionalDAO extends BaseDAO<Profissional> {
    public ProfissionalDAO() {
        super("profissionais.txt");
    }

    public void adicionar(Profissional profissional) {
        List<Profissional> profissionais = getAll();
        int nextId = profissionais.stream().mapToInt(Profissional::getId).max().orElse(0) + 1;
        profissional.setId(nextId);
        profissionais.add(profissional);
        saveAll(profissionais);
    }

    public void atualizar(Profissional profissional) {
        List<Profissional> profissionais = getAll();
        for (int i = 0; i < profissionais.size(); i++) {
            if (profissionais.get(i).getId() == profissional.getId()) {
                profissionais.set(i, profissional);
                break;
            }
        }
        saveAll(profissionais);
    }

    public void remover(int id) {
        List<Profissional> profissionais = getAll();
        profissionais.removeIf(p -> p.getId() == id);
        saveAll(profissionais);
    }

    public Profissional buscarPorId(int id) {
        return getAll().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
}
