import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Main {

    static class TimeDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null) {
                replace(fb, offset, 0, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;

            StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.replace(offset, offset + length, text);

            String digits = sb.toString().replaceAll("\\D", "");

            if (digits.length() > 4) {
                digits = digits.substring(0, 4);
            }

            String formatted = "";
            if (digits.length() <= 2) {
                formatted = digits;
            } else {
                formatted = digits.substring(0, 2) + ":" + digits.substring(2);
            }

            fb.replace(0, fb.getDocument().getLength(), formatted, attrs);
        }
    }

    static void addSelectAllOnFocus(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(field::selectAll);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculadora de Ponto");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(12, 2, 5, 5));

        String[] escalas = {
                "Sem escala",
                "08:00 - 11:10 - 12:20 - 17:10",
                "08:00 - 11:48 - 13:00 - 17:12",
                "08:00 - 11:30 - 13:30 - 18:00",
                "08:48 - 12:00 - 13:12 - 18:00",
                "08:00 - 12:00 - 13:12 - 17:12",
                "08:00 - 12:00 - 14:00 - 18:00",
                "09:18 - 13:00 - 14:12 - 18:30",
                "08:30 - 12:00 - 13:00 - 17:30",
                "08:15 - 12:30 - 13:45 - 17:30",
                "seg/qui 8:48/12:00 13:12/18:00 sex 8:00/11:12 13:12/18:00",
                "08:00 - 12:00 - 13:30 - 17:30",
                "08:00 - 12:30 - 14:00 - 17:30",
                "08:00 - 12:00 - 13:15 - 17:15",
                "08:15 - 12:00 - 13:10 - 17:25",
                "07:30 - 11:30 - 13:00 - 17:00",
                "08:00 - 12:00 - 13:00 - 17:00",
                "Seg a qua 09:00 - 12:00 - 13:00 - 18:00 Qui e Sex 08:00 - 12:00 - 13:30 - 17:30",
                "08:30 - 12:00 - 13:30 - 18:00",
                "09:30 - 12:00 - 14:00 - 19:30",
                "08:00 - 12:00 - 13:10 - 17:10",
                "08:00 - 11:45 - 13:00 - 17:15",
                "08:30 - 12:30 - 13:40 - 17:40",
                "07:00 - 12:00 - 13:12 - 16:12",
                "seg/qui 8:30/12:20 13:40/17:50 sex 8:10/12:20 13:40/17:30",
                "07:30 - 12:00 - 13:30 - 17:00",
                "08:30 - 12:00 - 14:00 - 18:30"
        };

        JComboBox<String> comboEscala = new JComboBox<>(escalas);
        JTextField campoCargaHoraria = new JTextField("08:00");
        JTextField campoSaldoAnterior = new JTextField("00:00");
        JCheckBox checkSaldoNegativo = new JCheckBox();

        JTextField campoChegada = new JTextField("00:00");
        JTextField campoSaidaAlmoco = new JTextField("00:00");
        JTextField campoVoltaAlmoco = new JTextField("00:00");
        JTextField campoSaidaFinal = new JTextField("00:00");

        JTextField[] camposHora = {campoCargaHoraria, campoSaldoAnterior, campoChegada, campoSaidaAlmoco, campoVoltaAlmoco};
        for (JTextField campo : camposHora) {
            ((PlainDocument) campo.getDocument()).setDocumentFilter(new TimeDocumentFilter());
            addSelectAllOnFocus(campo);
        }

        campoSaidaFinal.setEditable(false);

        frame.add(new JLabel("Escala"));
        frame.add(comboEscala);

        frame.add(new JLabel("Carga horária"));
        frame.add(campoCargaHoraria);

        frame.add(new JLabel("Saldo anterior"));
        frame.add(campoSaldoAnterior);

        frame.add(new JLabel("Saldo anterior é negativo?"));
        frame.add(checkSaldoNegativo);

        frame.add(new JLabel("Chegada"));
        frame.add(campoChegada);

        frame.add(new JLabel("Saída p/ almoço"));
        frame.add(campoSaidaAlmoco);

        frame.add(new JLabel("Volta do almoço"));
        frame.add(campoVoltaAlmoco);

        frame.add(new JLabel("Saída final (calculada)"));
        frame.add(campoSaidaFinal);

        JButton botaoCalcular = new JButton("Calcular");
        frame.add(botaoCalcular);

        final LocalTime[] escalaBase = new LocalTime[4];

        comboEscala.addActionListener(e -> {
            String escalaSelecionada = (String) comboEscala.getSelectedItem();
            if (!"Sem escala".equals(escalaSelecionada)) {
                try {
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");

                    if (escalaSelecionada.contains("seg/qui") || escalaSelecionada.contains("Seg a qua")) {
                        String[] opcoes = null;

                        if (escalaSelecionada.startsWith("seg/qui 8:48")) {
                            opcoes = new String[]{
                                    "08:48 - 12:00 - 13:12 - 18:00",
                                    "08:00 - 11:12 - 13:12 - 18:00"
                            };
                        } else if (escalaSelecionada.startsWith("Seg a qua 09:00")) {
                            opcoes = new String[]{
                                    "09:00 - 12:00 - 13:00 - 18:00",
                                    "08:00 - 12:00 - 13:30 - 17:30"
                            };
                        } else if (escalaSelecionada.startsWith("seg/qui 8:30")) {
                            opcoes = new String[]{
                                    "08:30 - 12:20 - 13:40 - 17:50",
                                    "08:10 - 12:20 - 13:40 - 17:30"
                            };
                        }

                        if (opcoes != null) {
                            String escolha = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Escolha a opção de horário:",
                                    "Seleção de Horário",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    opcoes,
                                    opcoes[0]
                            );

                            if (escolha != null) {
                                String[] partes = escolha.split("-");
                                for (int i = 0; i < 4; i++) escalaBase[i] = LocalTime.parse(partes[i].trim(), formato);

                                campoChegada.setText(partes[0].trim());
                                campoSaidaAlmoco.setText(partes[1].trim());
                                campoVoltaAlmoco.setText(partes[2].trim());
                            }
                        }
                    } else {
                        String[] partes = escalaSelecionada.split("-");
                        for (int i = 0; i < 4; i++) escalaBase[i] = LocalTime.parse(partes[i].trim(), formato);

                        campoChegada.setText(partes[0].trim());
                        campoSaidaAlmoco.setText(partes[1].trim());
                        campoVoltaAlmoco.setText(partes[2].trim());
                    }

                } catch (Exception ex) {
                    for (int i = 0; i < 4; i++) escalaBase[i] = null;
                    JOptionPane.showMessageDialog(null, "Erro ao carregar horários da escala selecionada.");
                }
            }
        });

        botaoCalcular.addActionListener(e -> {
            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime saldoAnteriorLT = LocalTime.parse(campoSaldoAnterior.getText(), formato);
                boolean saldoAnteriorNegativo = checkSaldoNegativo.isSelected();
                long saldoAnteriorMin = Duration.ofHours(saldoAnteriorLT.getHour())
                        .plusMinutes(saldoAnteriorLT.getMinute())
                        .toMinutes();
                if (saldoAnteriorNegativo) saldoAnteriorMin = -saldoAnteriorMin;

                LocalTime chegada = LocalTime.parse(campoChegada.getText(), formato);
                LocalTime saidaAlmoco = LocalTime.parse(campoSaidaAlmoco.getText(), formato);
                LocalTime voltaAlmoco = LocalTime.parse(campoVoltaAlmoco.getText(), formato);

                if (escalaBase[0] == null) {
                    JOptionPane.showMessageDialog(null, "Selecione uma escala válida.");
                    return;
                }

                long difEntrada     = Duration.between(chegada, escalaBase[0]).toMinutes();
                long difSaidaAlmoco = Duration.between(escalaBase[1], saidaAlmoco).toMinutes();
                long difVoltaAlmoco = Duration.between(voltaAlmoco, escalaBase[2]).toMinutes();

                long[] difs = new long[] { difEntrada, difSaidaAlmoco, difVoltaAlmoco };

                long adjustmentLarge = 0;
                boolean largePos = false, largeNeg = false;
                for (long d : difs) {
                    if (Math.abs(d) > 5) {
                        adjustmentLarge += d;
                        if (d > 0) largePos = true;
                        else largeNeg = true;
                    }
                }

                long provisionalSaldo = saldoAnteriorMin + adjustmentLarge;

                long ajuste = 0;
                for (long d : difs) {
                    long abs = Math.abs(d);
                    if (abs > 5) {
                        ajuste += d;
                        continue;
                    }

                    if ((d > 0 && largePos) || (d < 0 && largeNeg)) {
                        System.out.println("  Dif " + d + " ");
                        ajuste += d;
                        continue;
                    }

                    long newProvisionalAbs = Math.abs(provisionalSaldo + d);
                    long oldProvisionalAbs = Math.abs(provisionalSaldo);
                    if (newProvisionalAbs < oldProvisionalAbs) {
                        System.out.println("  Dif " + d);
                        ajuste += d;
                    } else {
                        System.out.println("  Dif " + d + " ignorada");
                    }
                }

                long saldoFinalMin = saldoAnteriorMin + ajuste;
                System.out.println("Saldo final (min): " + saldoFinalMin);

                LocalTime saidaPadrao = escalaBase[3];
                LocalTime saidaFinal  = saidaPadrao.minusMinutes(saldoFinalMin);

                campoSaidaFinal.setText(saidaFinal.format(formato));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao interpretar os horários. Use HH:mm.");
            }
        });
        frame.setVisible(true);
    }
}