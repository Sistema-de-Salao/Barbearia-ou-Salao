package view;

import controller.BarberController;
import model.NivelAcesso;
import model.Profissional;
import model.SessaoUsuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaProfissionais extends JPanel {
    private final BarberController controller;
    private final Runnable onBackToMain;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtEspecialidade;
    private JCheckBox chkAtivo;
    private int idSelecionado = -1;
    
    private JButton btnSalvar, btnEditar, btnRemover;

    public TelaProfissionais(BarberController controller, Runnable onBackToMain) {
        this.controller = controller;
        this.onBackToMain = onBackToMain;
        setLayout(new BorderLayout());
        setBackground(BarberStyle.COLOR_SECONDARY);
        initComponents();
        aplicarRestricoes();
        atualizarTabela();
    }

    private void initComponents() {

        JPanel header = BarberStyle.createHeader("Gestão de Profissionais");
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

        // Campos de entrada
        JPanel pnlInputs = new JPanel(new GridLayout(3, 2, 10, 12));
        pnlInputs.setOpaque(false);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(BarberStyle.FONT_LABEL);
        lblNome.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblNome);
        txtNome = new JTextField();
        pnlInputs.add(txtNome);

        JLabel lblEsp = new JLabel("Especialidade:");
        lblEsp.setFont(BarberStyle.FONT_LABEL);
        lblEsp.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblEsp);
        txtEspecialidade = new JTextField();
        pnlInputs.add(txtEspecialidade);

        JLabel lblAtivo = new JLabel("Ativo:");
        lblAtivo.setFont(BarberStyle.FONT_LABEL);
        lblAtivo.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblAtivo);
        chkAtivo = new JCheckBox("Sim", true);
        chkAtivo.setOpaque(false);
        pnlInputs.add(chkAtivo);

        pnlCadastroWrap.add(pnlInputs, BorderLayout.CENTER);

        // Botões alinhados verticalmente à direita dos campos
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

        // Tabela
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Especialidade", "Ativo"}, 0) {
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
                "Profissionais Cadastrados"
        ));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void aplicarRestricoes() {
        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();
        if (nivel != NivelAcesso.ADMINISTRADOR) {
            txtNome.setEnabled(false);
            txtEspecialidade.setEnabled(false);
            chkAtivo.setEnabled(false);
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
            txtEspecialidade.setText((String) tableModel.getValueAt(row, 2));
            chkAtivo.setSelected(tableModel.getValueAt(row, 3).equals("Sim"));
        }
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String especialidade = txtEspecialidade.getText().trim();

        if (!validarCampos(nome, especialidade, false)) return;

        controller.cadastrarProfissional(nome, especialidade);
        limparCampos();
        atualizarTabela();
    }

    private void editar() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um profissional na tabela para editar.");
            return;
        }
        String nome = txtNome.getText().trim();
        String especialidade = txtEspecialidade.getText().trim();

        if (!validarCampos(nome, especialidade, true)) return;

        controller.editarProfissional(idSelecionado, nome, especialidade, chkAtivo.isSelected());
        limparCampos();
        atualizarTabela();
    }

    private void remover() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um profissional na tabela para remover.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este profissional?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removerProfissional(idSelecionado);
            limparCampos();
            atualizarTabela();
        }
    }

    private boolean validarCampos(String nome, String especialidade, boolean isEdicao) {
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return false;
        }
        if (especialidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Especialidade é obrigatória.");
            return false;
        }

        if (verificarNomeDuplicado(nome, isEdicao)) {
            JOptionPane.showMessageDialog(this, "Este profissional já está cadastrado.");
            return false;
        }

        return true;
    }

    private boolean verificarNomeDuplicado(String nome, boolean isEdicao) {
        List<Profissional> profissionais = controller.listarProfissionais();
        for (Profissional p : profissionais) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                if (isEdicao && p.getId() == idSelecionado) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEspecialidade.setText("");
        chkAtivo.setSelected(true);
        idSelecionado = -1;
        table.clearSelection();
    }

    public void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Profissional> profissionais = controller.listarProfissionais();
        for (Profissional p : profissionais) {
            tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getEspecialidade(), p.isAtivo() ? "Sim" : "Não"});
        }
    }
}
