package controller;

import dao.*;
import model.Agendamento;
import model.Cliente;
import model.Profissional;
import model.Servico;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class BarberController {
    private final ClienteDAO clienteDAO;
    private final ProfissionalDAO profissionalDAO;
    private final ServicoDAO servicoDAO;
    private final AgendamentoDAO agendamentoDAO;
    private final HorarioProfissionalDAO horarioProfissionalDAO;

    public BarberController() {
        this.clienteDAO = new ClienteDAO();
        this.profissionalDAO = new ProfissionalDAO();
        this.servicoDAO = new ServicoDAO();
        this.agendamentoDAO = new AgendamentoDAO();
        this.horarioProfissionalDAO = new HorarioProfissionalDAO();
    }

    public void cadastrarCliente(String nome, String telefone, String email) {
        clienteDAO.adicionar(new Cliente(0, nome, telefone, email));
    }

    public void editarCliente(int id, String nome, String telefone, String email) {
        clienteDAO.atualizar(new Cliente(id, nome, telefone, email));
    }

    public void removerCliente(int id) {
        clienteDAO.remover(id);
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.getAll();
    }

    public void cadastrarProfissional(String nome, String especialidade) {
        profissionalDAO.adicionar(new Profissional(0, nome, especialidade, true));
    }

    public void editarProfissional(int id, String nome, String especialidade, boolean ativo) {
        profissionalDAO.atualizar(new Profissional(id, nome, especialidade, ativo));
    }

    public void removerProfissional(int id) {
        profissionalDAO.remover(id);
    }

    public List<Profissional> listarProfissionais() {
        return profissionalDAO.getAll();
    }


    public void cadastrarServico(String nome, int duracao, double preco) {
        servicoDAO.adicionar(new Servico(0, nome, duracao, preco));
    }

    public void editarServico(int id, String nome, int duracao, double preco) {
        servicoDAO.atualizar(new Servico(id, nome, duracao, preco));
    }

    public void removerServico(int id) {
        servicoDAO.remover(id);
    }

    public List<Servico> listarServicos() {
        return servicoDAO.getAll();
    }

    public boolean criarAgendamento(LocalDate data, LocalTime inicio, int idCliente, int idProfissional, int idServico) {
        Servico servico = servicoDAO.buscarPorId(idServico);
        LocalTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        if (temConflito(data, inicio, fim, idProfissional)) {
            return false;
        }

        agendamentoDAO.adicionar(new Agendamento(0, data, inicio, fim, "MARCADO", idCliente, idProfissional, idServico));
        return true;
    }

    private boolean temConflito(LocalDate data, LocalTime inicio, LocalTime fim, int idProfissional) {
        List<Agendamento> agendamentos = agendamentoDAO.getAll().stream()
                .filter(a -> a.getData().equals(data) && a.getIdProfissional() == idProfissional && !a.getStatus().equals("CANCELADO"))
                .collect(Collectors.toList());

        for (Agendamento a : agendamentos) {
            if (inicio.isBefore(a.getHoraFim()) && fim.isAfter(a.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }

    public List<Agendamento> listarAgendamentosPorData(LocalDate data) {
        return agendamentoDAO.getAll().stream()
                .filter(a -> a.getData().equals(data))
                .collect(Collectors.toList());
    }

    public void atualizarStatusAgendamento(int id, String status) {
        Agendamento a = agendamentoDAO.buscarPorId(id);
        if (a != null) {
            a.setStatus(status);
            agendamentoDAO.atualizar(a);
        }
    }

    public double calcularFaturacao(LocalDate inicio, LocalDate fim) {
        return agendamentoDAO.getAll().stream()
                .filter(a -> !a.getData().isBefore(inicio) && !a.getData().isAfter(fim) && a.getStatus().equals("CONCLUIDO"))
                .mapToDouble(a -> {
                    Servico s = servicoDAO.buscarPorId(a.getIdServico());
                    return s != null ? s.getPreco() : 0.0;
                }).sum();
    }

    public long contarAtendimentos(LocalDate inicio, LocalDate fim) {
        return agendamentoDAO.getAll().stream()
                .filter(a -> !a.getData().isBefore(inicio) && !a.getData().isAfter(fim) && a.getStatus().equals("CONCLUIDO"))
                .count();
    }

    public List<Agendamento> listarTodosAgendamentos() {
        return agendamentoDAO.getAll();
    }

    public ClienteDAO getClienteDAO() { return clienteDAO; }
    public ProfissionalDAO getProfissionalDAO() { return profissionalDAO; }
    public ServicoDAO getServicoDAO() { return servicoDAO; }
}
