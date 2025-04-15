import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Controleur extends JFrame {

    private String nomJoueur1 = "Joueur 1";
    private String nomJoueur2 = "Joueur 2";
    private int scoreJoueur1 = 0;
    private int scoreJoueur2 = 0;
    private JLabel labelScore;
    private JButton[][] cases = new JButton[3][3];
    private String[][] plateau = new String[3][3];
    private String symboleActuel = "-";
    private boolean contreOrdi = false;
    private JComboBox<String> choixTheme;
    private JButton boutonMode;
    private Color couleurBordure = Color.WHITE;

    public Controleur() {
        setTitle("Morpion");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nomJoueur1 = JOptionPane.showInputDialog(this, "Nom du joueur 1 :", "Joueur 1", JOptionPane.PLAIN_MESSAGE);
        nomJoueur2 = JOptionPane.showInputDialog(this, "Nom du joueur 2 :", "Joueur 2", JOptionPane.PLAIN_MESSAGE);
        if (nomJoueur1 == null || nomJoueur1.trim().isEmpty()) nomJoueur1 = "Joueur 1";
        if (nomJoueur2 == null || nomJoueur2.trim().isEmpty()) nomJoueur2 = "Joueur 2";

        JPanel panneauPrincipal = new JPanel(new BorderLayout());
        JPanel panneauGrille = new JPanel(new GridLayout(3, 3));
        JPanel panneauControle = new JPanel(new FlowLayout());

        labelScore = new JLabel("Score : " + nomJoueur1 + " " + scoreJoueur1 + " - " + nomJoueur2 + " " + scoreJoueur2);
        panneauControle.add(labelScore);

        JButton boutonRejouer = new JButton("Nouvelle Partie");
        boutonRejouer.addActionListener(e -> resetPartie());
        panneauControle.add(boutonRejouer);

        JButton boutonHistorique = new JButton("Historique");
        boutonHistorique.addActionListener(e -> afficherHistorique());
        panneauControle.add(boutonHistorique);

        boutonMode = new JButton("Mode : 2 Joueurs");
        boutonMode.addActionListener(e -> changerMode());
        panneauControle.add(boutonMode);

        choixTheme = new JComboBox<>(new String[]{"Sombre", "Aléatoire"});
        choixTheme.addActionListener(e -> changerTheme());
        panneauControle.add(choixTheme);

        panneauPrincipal.add(panneauControle, BorderLayout.NORTH);
        panneauPrincipal.add(panneauGrille, BorderLayout.CENTER);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cases[i][j] = new BoutonPerso();
                cases[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                cases[i][j].setOpaque(true);
                cases[i][j].setBorderPainted(false);
                cases[i][j].addActionListener(new ActionCase(i, j));
                panneauGrille.add(cases[i][j]);
            }
        }

        add(panneauPrincipal);
        resetPartie();
        changerTheme();
    }

    private void resetPartie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cases[i][j].setText("");
                plateau[i][j] = "";
            }
        }
        symboleActuel = "-";
        labelScore.setText("Score : " + nomJoueur1 + " " + scoreJoueur1 + " - " + nomJoueur2 + " " + scoreJoueur2);
    }

    private void changerMode() {
        contreOrdi = !contreOrdi;
        if (contreOrdi) {
            boutonMode.setText("Mode : vs Ordinateur");
            nomJoueur2 = "Ordinateur";
        } else {
            boutonMode.setText("Mode : 2 Joueurs");
            nomJoueur2 = "Joueur 2";
        }
        resetPartie();
    }

    private void changerTheme() {
        String themeChoisi = (String) choixTheme.getSelectedItem();
        if ("Sombre".equals(themeChoisi)) {
            getContentPane().setBackground(Color.BLACK);
            couleurBordure = Color.WHITE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cases[i][j].setBackground(Color.BLACK);
                    cases[i][j].setForeground(Color.WHITE);
                    ((BoutonPerso) cases[i][j]).setCouleurBordure(couleurBordure);
                }
            }
        } else {
            Random rand = new Random();
            Color couleurAleatoire = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            getContentPane().setBackground(couleurAleatoire);
            couleurBordure = Color.BLACK;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cases[i][j].setBackground(couleurAleatoire);
                    cases[i][j].setForeground(Color.BLACK);
                    ((BoutonPerso) cases[i][j]).setCouleurBordure(couleurBordure);
                }
            }
        }
        repaint();
    }

    private void verifierGagnant() {
        for (int i = 0; i < 3; i++) {
            if (plateau[i][0].equals(symboleActuel) && plateau[i][1].equals(symboleActuel) && plateau[i][2].equals(symboleActuel)) {
                finPartie();
                return;
            }
            if (plateau[0][i].equals(symboleActuel) && plateau[1][i].equals(symboleActuel) && plateau[2][i].equals(symboleActuel)) {
                finPartie();
                return;
            }
        }
        if (plateau[0][0].equals(symboleActuel) && plateau[1][1].equals(symboleActuel) && plateau[2][2].equals(symboleActuel)) {
            finPartie();
            return;
        }
        if (plateau[0][2].equals(symboleActuel) && plateau[1][1].equals(symboleActuel) && plateau[2][0].equals(symboleActuel)) {
            finPartie();
            return;
        }
        if (estPlein()) {
            JOptionPane.showMessageDialog(this, "Match nul !", "Fin", JOptionPane.INFORMATION_MESSAGE);
            resetPartie();
        }
    }

    private boolean estPlein() {
        for (String[] ligne : plateau) {
            for (String caseVal : ligne) {
                if (caseVal.isEmpty()) return false;
            }
        }
        return true;
    }

    private void finPartie() {
        String gagnant = symboleActuel.equals("X") ? nomJoueur1 : nomJoueur2;
        JOptionPane.showMessageDialog(this, gagnant + " a gagné !", "Fin", JOptionPane.INFORMATION_MESSAGE);
        if (symboleActuel.equals("X")) {
            scoreJoueur1++;
        } else {
            scoreJoueur2++;
        }
        enregistrerPartie(gagnant);
        resetPartie();
    }

    private void enregistrerPartie(String gagnant) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historique.txt", true))) {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            writer.write(date + " - " + nomJoueur1 + " vs " + nomJoueur2 + " - Gagnant : " + gagnant + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur sauvegarde.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ActionCase implements ActionListener {
        private int ligne, colonne;

        public ActionCase(int ligne, int colonne) {
            this.ligne = ligne;
            this.colonne = colonne;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cases[ligne][colonne].getText().isEmpty()) {
                cases[ligne][colonne].setText(symboleActuel);
                plateau[ligne][colonne] = symboleActuel;
                verifierGagnant();
                symboleActuel = symboleActuel.equals("-") ? "+" : "-";
                if (contreOrdi && symboleActuel.equals("+")) {
                    jouerOrdi();
                }
            }
        }
    }

    private void jouerOrdi() {
        Random rand = new Random();
        int ligne, colonne;
        do {
            ligne = rand.nextInt(3);
            colonne = rand.nextInt(3);
        } while (!plateau[ligne][colonne].isEmpty());
        cases[ligne][colonne].setText("O");
        plateau[ligne][colonne] = "+";
        verifierGagnant();
        symboleActuel = "-";
    }

    private class BoutonPerso extends JButton {
        private static final long serialVersionUID = 1L;
        private Color couleurBordure;

        public BoutonPerso() {
            setContentAreaFilled(false);
        }

        public void setCouleurBordure(Color couleur) {
            this.couleurBordure = couleur;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(couleurBordure);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private void afficherHistorique() {
        try (BufferedReader reader = new BufferedReader(new FileReader("historique.txt"))) {
            StringBuilder contenu = new StringBuilder();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                contenu.append(ligne).append("\n");
            }
            JOptionPane.showMessageDialog(this, contenu.toString(), "Historique", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Aucun historique dispo.", "Historique", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Controleur().setVisible(true));
    }
}
