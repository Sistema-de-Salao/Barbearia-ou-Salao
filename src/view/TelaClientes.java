package view;

import controller.BarberController;
import model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaClientes extends JPanel {
    private final BarberController controller;
    private final Runnable onBackToMain;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtTelefone, txtEmail;
    private int idSelecionado = -1;

    public TelaClientes(BarberController controller, Runnable onBackToMain) {
        this.controller = controller;
        this.onBackToMain = onBackToMain;
        setLayout(new BorderLayout());
        setBackground(BarberStyle.COLOR_SECONDARY);
        initComponents();
        atualizarTabela();
    }

    private void initComponents() {
        JPanel header = BarberStyle.createHeader("Gestão de Clientes");
        JButton btnVoltar = new JButton("Voltar ao Menu");
        BarberStyle.applyBackButtonStyle(btnVoltar);
        btnVoltar.addActionListener(e -> onBackToMain.run());
        header.add(btnVoltar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(BarberStyle.COLOR_SECONDARY);

        JPanel pnlCadastroWrap = new JPanel(new BorderLayout(15, 0));
        pnlCadastroWrap.setBackground(BarberStyle.COLOR_WHITE);
        pnlCadastroWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlInputs = new JPanel(new GridLayout(3, 2, 10, 12));
        pnlInputs.setOpaque(false);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(BarberStyle.FONT_LABEL);
        lblNome.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblNome);
        txtNome = new JTextField();
        pnlInputs.add(txtNome);

        JLabel lblTelefone = new JLabel("Telefone:");
        lblTelefone.setFont(BarberStyle.FONT_LABEL);
        lblTelefone.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblTelefone);
        txtTelefone = new JTextField();
        pnlInputs.add(txtTelefone);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(BarberStyle.FONT_LABEL);
        lblEmail.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblEmail);
        txtEmail = new JTextField();
        pnlInputs.add(txtEmail);

        pnlCadastroWrap.add(pnlInputs, BorderLayout.CENTER);

        JPanel pnlBotoes = new JPanel();
        pnlBotoes.setLayout(new GridLayout(3, 1, 0, 8));
        pnlBotoes.setOpaque(false);
        pnlBotoes.setBorder(new EmptyBorder(0, 10, 0, 0));

        JButton btnSalvar = new JButton("Cadastrar");
        BarberStyle.applyButtonStyle(btnSalvar);
        btnSalvar.addActionListener(e -> salvar());

        JButton btnEditar = new JButton("Salvar Edição");
        BarberStyle.applyButtonStyle(btnEditar);
        btnEditar.addActionListener(e -> editar());

        JButton btnRemover = new JButton("Remover");
        BarberStyle.applyDangerButtonStyle(btnRemover);
        btnRemover.addActionListener(e -> remover());

        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnEditar);
        pnlBotoes.add(btnRemover);

        pnlCadastroWrap.add(pnlBotoes, BorderLayout.EAST);
        centerPanel.add(pnlCadastroWrap, BorderLayout.NORTH);


        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Telefone", "Email"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(BarberStyle.FONT_LABEL);
        table.getTableHeader().setBackground(BarberStyle.COLOR_PRIMARY);
        table.getTableHeader().setForeground(BarberStyle.COLOR_WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarDadosSelecionados();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_ACCENT),
                "Clientes Cadastrados"
        ));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void carregarDadosSelecionados() {
        int row = table.getSelectedRow();
        if (row != -1) {
            idSelecionado = (int) tableModel.getValueAt(row, 0);
            txtNome.setText((String) tableModel.getValueAt(row, 1));
            txtTelefone.setText((String) tableModel.getValueAt(row, 2));
            txtEmail.setText((String) tableModel.getValueAt(row, 3));
        }
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();

        if (!validarCampos(nome, telefone, email, false)) return;

        controller.cadastrarCliente(nome, telefone, email);
        limparCampos();
        atualizarTabela();
    }

    private void editar() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para editar.");
            return;
        }
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();

        if (!validarCampos(nome, telefone, email, true)) return;

        controller.editarCliente(idSelecionado, nome, telefone, email);
        limparCampos();
        atualizarTabela();
    }

    private void remover() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para remover.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removerCliente(idSelecionado);
            limparCampos();
            atualizarTabela();
        }
    }

    private boolean validarCampos(String nome, String telefone, String email, boolean isEdicao) {
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return false;
        }
        if (telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Telefone é obrigatório.");
            return false;
        }

        if (!validarTelefone(telefone)) {
            JOptionPane.showMessageDialog(this, "Telefone inválido. Use formato: (+258) XXX-XXX-XXX ou similar");
            return false;
        }

        if (!email.isEmpty() && !validarEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email inválido. Use formato correto: usuario@dominio.com");
            return false;
        }

        if (verificarTelefoneDuplicado(telefone, isEdicao)) {
            JOptionPane.showMessageDialog(this, "Este telefone já está cadastrado.");
            return false;
        }
        if (!email.isEmpty() && verificarEmailDuplicado(email, isEdicao)) {
            JOptionPane.showMessageDialog(this, "Este email já está cadastrado.");
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean validarTelefone(String telefone) {
        return telefone.matches("^[0-9()\\s-+]+$") && telefone.length() >= 10;
    }

    private boolean verificarTelefoneDuplicado(String telefone, boolean isEdicao) {
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            if (c.getTelefone().equals(telefone)) {
                if (isEdicao && c.getId() == idSelecionado) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private boolean verificarEmailDuplicado(String email, boolean isEdicao) {
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            if (c.getEmail().equals(email)) {
                if (isEdicao && c.getId() == idSelecionado) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        idSelecionado = -1;
        table.clearSelection();
    }

    public void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail()});
        }
    }
}