import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class jonesspeakGUI extends Application{
	private BorderPane evalPane;
    private GridPane evalResults;	
	private Label evalLabel;
	private TextField expression;
	private Text outputResults;
	private Button evaluate;
	private HBox btnBox;
	private static jonesspeak translator;
	private HBox radioBtnBox;
    private Button encrypt;
    private Button decrypt;

	
	public jonesspeakGUI() {
		translator = new jonesspeak();
	}
	
	private void initLabels_Text_TextFields() {
		evalLabel = new Label("JonesSpeak: Dictionary Encryption");
		evalLabel.setFont(Font.font("Cambria Math",20));
	    outputResults = new Text(0,0,"");
	    outputResults.setFont(Font.font("Consolas",18));
		expression = new TextField();
		expression.setPrefWidth(600);
		expression.setFont(Font.font("Consolas",18));	
	}
	
	private void evaluate(String typeSelected) {

		String results="There was an error"; 
		outputResults.setFill(Color.BLACK);
		
		if(expression.getText().equals("")) {
			results = "Please fill the text field";
			outputResults.setFill(Color.INDIANRED);
		}
		else {		
		try {
		if(typeSelected.equals("e")) {
			results = translator.encrypt(expression.getText());}
		else {
			results = translator.decrypt(expression.getText());}
		if(results == null) {
			results = "Your message cannot be decrypted";
			outputResults.setFill(Color.INDIANRED);
		}
		else {
			
			String notIncluded = "";
			String[] wordArray = results.split("\s");
			String[] inputArray = expression.getText().split("\s");
			for(int i = 0; i<wordArray.length; i++) {
				if(wordArray[i].equals("-1")) {
					notIncluded = notIncluded + ", "+inputArray[i];
				}
			}
			
			if(!notIncluded.equals("")) {
				results = "The following words are not in JonesSpeak: "+ notIncluded.substring(1);
				outputResults.setFill(Color.INDIANRED);
			}			
		}

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		outputResults.setText(results);


	}
	
	private void initButtons_HBox() {
		encrypt = new Button("Encrypt");
    	decrypt = new Button("Decrypt");
    	encrypt.setStyle("-fx-background-color: #0096D6; -fx-text-fill: White;");
    	decrypt.setStyle("-fx-background-color: MediumSeaGreen; -fx-text-fill: White;");
    	
    	decrypt.setPrefSize(150, 25);
    	encrypt.setPrefSize(150, 25);
    	
		encrypt.setOnMouseClicked(e -> evaluate("e"));
		decrypt.setOnMouseClicked(e -> evaluate("d"));

		btnBox = new HBox(15);
		btnBox.getChildren().addAll(encrypt, decrypt);
		btnBox.setAlignment(Pos.CENTER);
	}

	/**
	 * Inits the grid pane and populates it.
	 */
	private void initGridPane() {
		evalResults = new GridPane();
		evalResults.add(evalLabel, 0, 0);
		evalResults.add(expression,0,1);
		evalResults.add(btnBox,0,2);
		evalResults.add(outputResults,0,3);
		
		evalResults.setHgap(10); 
		evalResults.setVgap(10); 
		evalResults.setPadding(new Insets(10, 10, 10, 10)); 
		GridPane.setHalignment(evalLabel, HPos.CENTER);
		GridPane.setHalignment(outputResults, HPos.CENTER);		
	}
	
	public static void main(String[] args) throws IOException {
		Application.launch(args);

	}
	
	public void start(Stage primaryStage) throws Exception {
		translator.formatDictionary();	
		initLabels_Text_TextFields();
		initButtons_HBox();
		initGridPane();	
		evalPane = new BorderPane();		
		evalPane.setCenter(evalResults);
		evalPane.setBackground(new Background(new BackgroundFill(Color.web("eeeeee"), null, null)));

		Scene scene = new Scene(evalPane);

		primaryStage.setTitle("JonesSpeak v1.1.0");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	

}
