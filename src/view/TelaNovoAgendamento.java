package view;

import controller.BarberController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaNovoAgendamento extends JDialog {
    private final BarberController controller;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Profissional> cbProfissional;
    private JComboBox<Servico> cbServico;
    private JTextField txtData, txtHora;
    private boolean salvo = false;

    public TelaNovoAgendamento(Frame owner, BarberController controller) {
        super(owner, "Novo Agendamento", true);
        this.controller = controller;
        initComponents();
        carregarCombos();
        aplicarRestricoes();
        setSize(480, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BarberStyle.COLOR_WHITE);

        JPanel pnlInputs = new JPanel(new GridLayout(5, 2, 10, 12));
        pnlInputs.setOpaque(false);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(BarberStyle.FONT_LABEL);
        lblCliente.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblCliente);
        cbCliente = new JComboBox<>();
        pnlInputs.add(cbCliente);

        JLabel lblProf = new JLabel("Profissional:");
        lblProf.setFont(BarberStyle.FONT_LABEL);
        lblProf.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblProf);
        cbProfissional = new JComboBox<>();
        pnlInputs.add(cbProfissional);

        JLabel lblServ = new JLabel("Serviço:");
        lblServ.setFont(BarberStyle.FONT_LABEL);
        lblServ.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblServ);
        cbServico = new JComboBox<>();
        pnlInputs.add(cbServico);

        JLabel lblData = new JLabel("Data:");
        lblData.setFont(BarberStyle.FONT_LABEL);
        lblData.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblData);
        txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        pnlInputs.add(txtData);

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        lblHora.setFont(BarberStyle.FONT_LABEL);
        lblHora.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblHora);
        txtHora = new JTextField("09:00");
        pnlInputs.add(txtHora);

        mainPanel.add(pnlInputs, BorderLayout.CENTER);


        JPanel pnlBotoes = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBotoes.setOpaque(false);
        pnlBotoes.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnSalvar = new JButton("Confirmar Agendamento");
        BarberStyle.applyButtonStyle(btnSalvar);
        btnSalvar.addActionListener(e -> salvar());

        JButton btnCancelar = new JButton("Cancelar");
        BarberStyle.applyBackButtonStyle(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnCancelar);

        mainPanel.add(pnlBotoes, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void carregarCombos() {
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) cbCliente.addItem(c);

        List<Profissional> profissionais = controller.listarProfissionais();
        for (Profissional p : profissionais) cbProfissional.addItem(p);

        List<Servico> servicos = controller.listarServicos();
        for (Servico s : servicos) cbServico.addItem(s);
    }

    private void aplicarRestricoes() {
        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();
        int idVinculado = SessaoUsuario.getInstance().getIdVinculado();

        if (nivel == NivelAcesso.CLIENTE) {
            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                Cliente c = cbCliente.getItemAt(i);
                if (c.getId() == idVinculado) {
                    cbCliente.setSelectedIndex(i);
                    break;
                }
            }
            cbCliente.setEnabled(false);
        }
    }

    private void salvar() {
        try {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Profissional prof = (Profissional) cbProfissional.getSelectedItem();
            Servico serv = (Servico) cbServico.getSelectedItem();

            if (cliente == null || prof == null || serv == null) {
                JOptionPane.showMessageDialog(this, "Selecione cliente, profissional e serviço.");
                return;
            }

            String dataStr = txtData.getText().trim();
            String horaStr = txtHora.getText().trim();

            if (!validarCampos(dataStr, horaStr)) return;

            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));

            if (data.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "A data do agendamento não pode ser no passado.");
                return;
            }

            boolean sucesso = controller.criarAgendamento(data, hora, cliente.getId(), prof.getId(), serv.getId());
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Agendamento realizado com sucesso!");
                salvo = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Conflito de horário! O profissional já está ocupado neste período.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados. Use formato dd/MM/yyyy para data e HH:mm para hora.");
        }
    }

    private boolean validarCampos(String data, String hora) {
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data é obrigatória.");
            return false;
        }

        if (hora.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hora é obrigatória.");
            return false;
        }

        // Validar formato de data
        if (!data.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use formato: dd/MM/yyyy");
            return false;
        }

        // Validar formato de hora
        if (!hora.matches("^\\d{2}:\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Hora inválida. Use formato: HH:mm");
            return false;
        }

        try {
            LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data ou hora inválida.");
            return false;
        }

        return true;
    }

    public boolean isSalvo() { return salvo; }
}
