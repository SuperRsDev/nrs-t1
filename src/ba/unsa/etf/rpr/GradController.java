package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public TextField fieldPostanskiBroj;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    private Grad grad;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            fieldPostanskiBroj.setText(grad.getPostanskiBroj());
            // choiceDrzava.getSelectionModel().select(grad.getDrzava());
            // ovo ne radi jer grad.getDrzava() nije identički jednak objekat kao član listDrzave
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public boolean validirajPostanskiBroj() {
        String adresa = "http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + fieldPostanskiBroj.getText();
        try {
            URL url = new URL(adresa);
            BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            StringBuilder odgovor = new StringBuilder();
            String line = null;
            while ((line = ulaz.readLine()) != null)
                odgovor.append(line);

            System.out.println("odogovor: " + odgovor);
            ulaz.close();
            return odgovor.toString().equals("OK");
        } catch(MalformedURLException e) {
            System.out.println("String "+adresa+" ne predstavlja validan URL");
        } catch(IOException e) {
            System.out.println("Greška pri kreiranju ulaznog toka");
            System.out.println(e.getMessage());
        } catch(Exception e) {
            System.out.println("Poteškoće sa obradom JSON podataka");
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void clickDodajZnamenitost(ActionEvent actionEvent) {

    }

    public void clickOk(ActionEvent actionEvent) {
        AtomicBoolean sveOk = new AtomicBoolean(true);

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk.set(false);
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }


        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk.set(false);
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }

        Thread thread = new Thread(() -> {
            sveOk.set(this.validirajPostanskiBroj());

            if (!sveOk.get()) {
                fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
                return;
            } else {
                fieldPostanskiBroj.getStyleClass().removeAll("poljeNijeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeIspravno");
            }

            this.postaviGrad();
        });
        thread.start();
    }

    private void postaviGrad() {
        if (grad == null) grad = new Grad();
        grad.setNaziv(fieldNaziv.getText());
        grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        grad.setPostanskiBroj(fieldPostanskiBroj.getText());
        grad.setDrzava(choiceDrzava.getValue());
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }
}
