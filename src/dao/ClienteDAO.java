package dao;

import model.Cliente;
import java.util.List;

public class ClienteDAO extends BaseDAO<Cliente> {
    public ClienteDAO() {
        super("clientes.txt");
    }

    public void adicionar(Cliente cliente) {
        List<Cliente> clientes = getAll();
        int nextId = clientes.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
        cliente.setId(nextId);
        clientes.add(cliente);
        saveAll(clientes);
    }

    public void atualizar(Cliente cliente) {
        List<Cliente> clientes = getAll();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                break;
            }
        }
        saveAll(clientes);
    }

    public void remover(int id) {
        List<Cliente> clientes = getAll();
        clientes.removeIf(c -> c.getId() == id);
        saveAll(clientes);
    }

    public Cliente buscarPorId(int id) {
        return getAll().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
