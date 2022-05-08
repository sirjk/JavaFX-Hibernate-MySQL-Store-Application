package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

    static boolean answer;

    public static boolean display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);

        Button yesBtn = new Button("Tak");
        yesBtn.setMinWidth(70);
        Button noBtn = new Button("Nie");
        noBtn.setMinWidth(70);

        yesBtn.setOnAction(e->{
            answer = true;
            window.close();
        });

        noBtn.setOnAction(e->{
            answer = false;
            window.close();
        });

        HBox layout = new HBox(10);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(yesBtn,noBtn);
        layout.setAlignment(Pos.CENTER);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.getChildren().addAll(label,layout);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
