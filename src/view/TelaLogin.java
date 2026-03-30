package view;

import controller.BarberController;
import model.NivelAcesso;
import model.Profissional;
import model.SessaoUsuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class TelaLogin extends JDialog {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JLabel lblErro;
    private boolean autenticado = false;
    private final BarberController controller;

    public TelaLogin() {
        super((Frame) null, "BarberSystem - Login", true);
        this.controller = new BarberController();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel pnlTopo = new JPanel(new GridBagLayout());
        pnlTopo.setBackground(new Color(0, 102, 204));
        pnlTopo.setPreferredSize(new Dimension(0, 140));

        JLabel lblIcone = new JLabel("✂️");
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcone.setForeground(Color.WHITE);

        pnlTopo.add(lblIcone, new GridBagConstraints());

        JPanel pnlCorpo = new JPanel();
        pnlCorpo.setBackground(Color.WHITE);
        pnlCorpo.setLayout(new BoxLayout(pnlCorpo, BoxLayout.Y_AXIS));
        pnlCorpo.setBorder(new EmptyBorder(35, 45, 20, 45));

        txtUsuario = new JTextField();
        txtUsuario.setBorder(new EmptyBorder(5, 10, 5, 10));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JPanel pnlUserWrapper = createInputWrapper("👤 Usuário", txtUsuario);

        txtSenha = new JPasswordField();
        txtSenha.setBorder(new EmptyBorder(5, 10, 5, 10));
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSenha.addActionListener(e -> tentarLogin());
        JPanel pnlPassWrapper = createInputWrapper("🔒 Senha", txtSenha);

        lblErro = new JLabel(" ");
        lblErro.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lblErro.setForeground(new Color(220, 53, 69));
        lblErro.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(new Color(0, 102, 204));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setPreferredSize(new Dimension(200, 50));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.addActionListener(e -> tentarLogin());

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBackground(new Color(108, 117, 125));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVoltar.setPreferredSize(new Dimension(200, 50));
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> dispose());

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlButton.setOpaque(false);
        pnlButton.add(btnEntrar);
        pnlButton.add(btnVoltar);

        JLabel lblRodape = new JLabel("<html><center><font color='#888888'>🔑 Senha padrão: <b>1234</b></font><br>"
                + "<font color='#AAAAAA'>💡 Dica: Use seu nome cadastrado</font></center></html>");
        lblRodape.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JPanel pnlRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlRodape.setOpaque(false);
        pnlRodape.add(lblRodape);

        pnlCorpo.add(pnlUserWrapper);
        pnlCorpo.add(Box.createVerticalStrut(20));
        pnlCorpo.add(pnlPassWrapper);
        pnlCorpo.add(Box.createVerticalStrut(15));
        pnlCorpo.add(lblErro);
        pnlCorpo.add(Box.createVerticalStrut(15));
        pnlCorpo.add(pnlButton);
        pnlCorpo.add(Box.createVerticalGlue());
        pnlCorpo.add(pnlRodape);

        root.add(pnlTopo, BorderLayout.NORTH);
        root.add(pnlCorpo, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createInputWrapper(String title, JComponent component) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI Emoji", Font.BOLD, 12),
                new Color(100, 100, 100)
        );

        pnl.setBorder(border);
        pnl.add(component, BorderLayout.CENTER);

        return pnl;
    }

    private void tentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String senha   = new String(txtSenha.getPassword()).trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            lblErro.setText("⚠️ Preencha todos os campos.");
            return;
        }

        if (usuario.equalsIgnoreCase(ADMIN_USER) && senha.equals(ADMIN_PASS)) {
            SessaoUsuario.getInstance().login(usuario, NivelAcesso.ADMINISTRADOR, -1);
            finalizarLogin();
            return;
        }

        if (senha.equals("1234")) {
            List<Profissional> profissionais = controller.listarProfissionais();
            for (Profissional p : profissionais) {
                if (p.getNome().equalsIgnoreCase(usuario)) {
                    SessaoUsuario.getInstance().login(p.getNome(), NivelAcesso.FUNCIONARIO, p.getId());
                    finalizarLogin();
                    return;
                }
            }
        }

        lblErro.setText("❌ Usuário ou senha incorretos.");
        txtSenha.setText("");
        txtSenha.requestFocus();
    }

    private void finalizarLogin() {
        autenticado = true;
        dispose();
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}