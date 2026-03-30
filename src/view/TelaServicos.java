package view;

import controller.BarberController;
import model.NivelAcesso;
import model.Servico;
import model.SessaoUsuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaServicos extends JPanel {
    private final BarberController controller;
    private final Runnable onBackToMain;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtDuracao, txtPreco;
    private int idSelecionado = -1;
    
    private JButton btnSalvar, btnEditar, btnRemover;

    public TelaServicos(BarberController controller, Runnable onBackToMain) {
        this.controller = controller;
        this.onBackToMain = onBackToMain;
        setLayout(new BorderLayout());
        setBackground(BarberStyle.COLOR_SECONDARY);
        initComponents();
        aplicarRestricoes();
        atualizarTabela();
    }

    private void initComponents() {
        // Cabeçalho
        JPanel header = BarberStyle.createHeader("Gestão de Serviços");
        JButton btnVoltar = new JButton("Voltar ao Menu");
        BarberStyle.applyBackButtonStyle(btnVoltar);
        btnVoltar.addActionListener(e -> onBackToMain.run());
        header.add(btnVoltar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Painel Central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(BarberStyle.COLOR_SECONDARY);

        // Painel de Cadastro: campos à esquerda, botões à direita
        JPanel pnlCadastroWrap = new JPanel(new BorderLayout(15, 0));
        pnlCadastroWrap.setBackground(BarberStyle.COLOR_WHITE);
        pnlCadastroWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Campos de entrada
        JPanel pnlInputs = new JPanel(new GridLayout(3, 2, 10, 12));
        pnlInputs.setOpaque(false);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(BarberStyle.FONT_LABEL);
        lblNome.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblNome);
        txtNome = new JTextField();
        pnlInputs.add(txtNome);

        JLabel lblDuracao = new JLabel("Duração (min):");
        lblDuracao.setFont(BarberStyle.FONT_LABEL);
        lblDuracao.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblDuracao);
        txtDuracao = new JTextField();
        pnlInputs.add(txtDuracao);

        JLabel lblPreco = new JLabel("Preço (MT):");
        lblPreco.setFont(BarberStyle.FONT_LABEL);
        lblPreco.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblPreco);
        txtPreco = new JTextField();
        pnlInputs.add(txtPreco);

        pnlCadastroWrap.add(pnlInputs, BorderLayout.CENTER);

        JPanel pnlBotoes = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlBotoes.setOpaque(false);
        pnlBotoes.setBorder(new EmptyBorder(0, 10, 0, 0));

        btnSalvar = new JButton("Cadastrar");
        BarberStyle.applyButtonStyle(btnSalvar);
        btnSalvar.addActionListener(e -> salvar());

        btnEditar = new JButton("Salvar Edição");
        BarberStyle.applyButtonStyle(btnEditar);
        btnEditar.addActionListener(e -> editar());

        btnRemover = new JButton("Remover");
        BarberStyle.applyDangerButtonStyle(btnRemover);
        btnRemover.addActionListener(e -> remover());

        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnEditar);
        pnlBotoes.add(btnRemover);

        pnlCadastroWrap.add(pnlBotoes, BorderLayout.EAST);
        centerPanel.add(pnlCadastroWrap, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Duração", "Preço"}, 0) {
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
                "Serviços Disponíveis"
        ));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void aplicarRestricoes() {
        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();
        if (nivel != NivelAcesso.ADMINISTRADOR) {
            txtNome.setEnabled(false);
            txtDuracao.setEnabled(false);
            txtPreco.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnRemover.setEnabled(false);
        }
    }

    private void carregarDadosSelecionados() {
        int row = table.getSelectedRow();
        if (row != -1) {
            idSelecionado = (int) tableModel.getValueAt(row, 0);
            txtNome.setText((String) tableModel.getValueAt(row, 1));
            String duracaoStr = (String) tableModel.getValueAt(row, 2);
            txtDuracao.setText(duracaoStr.replace(" min", ""));
            String precoStr = (String) tableModel.getValueAt(row, 3);
            txtPreco.setText(precoStr.replace("MT ", "").replace(",", "."));
        }
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String durationStr = txtDuracao.getText().trim();
        String priceStr = txtPreco.getText().trim();

        if (!validarCampos(nome, durationStr, priceStr, false)) return;

        try {
            int duracao = Integer.parseInt(durationStr);
            double preco = Double.parseDouble(priceStr);
            controller.cadastrarServico(nome, duracao, preco);
            limparCampos();
            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage());
        }
    }

    private void editar() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela para editar.");
            return;
        }
        String nome = txtNome.getText().trim();
        String durationStr = txtDuracao.getText().trim();
        String priceStr = txtPreco.getText().trim();

        if (!validarCampos(nome, durationStr, priceStr, true)) return;

        try {
            int duracao = Integer.parseInt(durationStr);
            double preco = Double.parseDouble(priceStr);
            controller.editarServico(idSelecionado, nome, duracao, preco);
            limparCampos();
            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage());
        }
    }

    private void remover() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela para remover.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este serviço?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removerServico(idSelecionado);
            limparCampos();
            atualizarTabela();
        }
    }

    private boolean validarCampos(String nome, String duracao, String preco, boolean isEdicao) {
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return false;
        }

        if (duracao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Duração é obrigatória.");
            return false;
        }

        if (preco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preço é obrigatório.");
            return false;
        }

        try {
            int durInt = Integer.parseInt(duracao);
            if (durInt <= 0) {
                JOptionPane.showMessageDialog(this, "Duração deve ser um número positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Duração deve ser um número inteiro.");
            return false;
        }

        try {
            double precoDouble = Double.parseDouble(preco);
            if (precoDouble <= 0) {
                JOptionPane.showMessageDialog(this, "Preço deve ser um valor positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço deve ser um número decimal válido.");
            return false;
        }

        if (verificarNomeDuplicado(nome, isEdicao)) {
            JOptionPane.showMessageDialog(this, "Este serviço já está cadastrado.");
            return false;
        }

        return true;
    }

    private boolean verificarNomeDuplicado(String nome, boolean isEdicao) {
        List<Servico> servicos = controller.listarServicos();
        for (Servico s : servicos) {
            if (s.getNome().equalsIgnoreCase(nome)) {
                if (isEdicao && s.getId() == idSelecionado) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private void limparCampos() {
        txtNome.setText("");
        txtDuracao.setText("");
        txtPreco.setText("");
        idSelecionado = -1;
        table.clearSelection();
    }

    public void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Servico> servicos = controller.listarServicos();
        for (Servico s : servicos) {
            tableModel.addRow(new Object[]{s.getId(), s.getNome(), s.getDuracaoMinutos() + " min", String.format("MT %.2f", s.getPreco())});
        }
    }
}
