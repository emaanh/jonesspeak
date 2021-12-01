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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private String typeSelected = "e";
    private RadioButton encrypt;
    private RadioButton decrypt;

	
	public jonesspeakGUI() {
		translator = new jonesspeak();
	}
	
	private void initLabels_Text_TextFields() {
		evalLabel = new Label("Welcome to JonesSpeak");
		evalLabel.setFont(Font.font("Cambria Math",18));
	    outputResults = new Text(0,0,"");
	    outputResults.setFont(Font.font("Cambria Math",18));
		expression = new TextField();
		expression.setPrefWidth(600);
		expression.setFont(Font.font("Cambria Math",18));	
	}
	
	private void initButtons_HBox() {
		evaluate = new Button("Evaluate");
		encrypt = new RadioButton("Encrypt");
    	decrypt = new RadioButton("Decrypt");
    	
		encrypt.setOnAction(e -> typeSelected = "e");
		decrypt.setOnAction(e -> typeSelected = "d");
		
		evaluate.setOnMouseClicked(e -> {
			
			String results="There was an error"; 
			try {
			if(typeSelected.equals("e")) {
				results = translator.encrypt(expression.getText());}
			else {
				results = translator.decrypt(expression.getText());}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String notIncluded = "";
			if(!results.equals("There was an error")) {

			String[] wordArray = results.split("\s");
			String[] inputArray = expression.getText().split("\s");
			for(int i = 0; i<wordArray.length; i++) {
				if(wordArray[i].equals("-1")) {
					notIncluded = notIncluded + ", "+inputArray[i];
				}
			}
			
			if(!notIncluded.equals("")) {
				results = "The following words are not in JonesSpeak: "+ notIncluded.substring(1);
			}
			}
			outputResults.setText(results);
			
		});
		
    	ToggleGroup tg = new ToggleGroup();	
    	encrypt.setToggleGroup(tg);
    	decrypt.setToggleGroup(tg);
    	encrypt.setSelected(true); 
 
		radioBtnBox = new HBox(15);
		radioBtnBox.getChildren().addAll(encrypt, decrypt);
		radioBtnBox.setAlignment(Pos.CENTER);

		btnBox = new HBox(15);
		btnBox.getChildren().add(evaluate);
		btnBox.setAlignment(Pos.CENTER);
	}

	/**
	 * Inits the grid pane and populates it.
	 */
	private void initGridPane() {
		evalResults = new GridPane();
		evalResults.add(evalLabel, 0, 0);
		evalResults.add(radioBtnBox, 0, 1);
		evalResults.add(expression,0,2);
	    evalResults.add(new Label(""), 0, 3);
		evalResults.add(outputResults,0,4);
		evalResults.add(btnBox,0,5);
		
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
//		evalPane.setTop(evalLabel);
		
		evalPane.setCenter(evalResults);
//		evalPane.setBottom(btnBox);		
		Scene scene = new Scene(evalPane, 600,150);
		primaryStage.setTitle("JonesSpeak");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	

}
