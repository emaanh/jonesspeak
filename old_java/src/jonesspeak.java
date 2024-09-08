import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class jonesspeak extends Application{
	private BorderPane evalPane;
    private GridPane evalResults;	
	private Label evalLabel;
	private TextField expression;
	private Text stats;
	private Text outputResults;
	private Button evaluate;
	private HBox btnBox;
	private Huffman translator;
	private HBox radioBtnBox;
    private Button encrypt;
    private Button decrypt;
    private String version;
    
    
	public jonesspeak() throws IOException {
		version = "2.0.0";
	}
	
	
	private void evaluate(String mode) {
		String results;
		outputResults.setFill(Color.BLACK);

		
		String input = expression.getText();
//		.replaceAll(". ", " . ").replaceAll("\s+","\s");
		if(errorCheckingPre(input)) {;

		if(mode.equals("e")) {
			results = translator.encode(input).toLowerCase();
			
			stats.setText("Ratio (excluding space): "+Math.round((results.length()*1.0)/input.replaceAll(" ","").length()*10000)/100.0+"%"
			+"\nRatio (including space): "+Math.round((results.length()*1.0)/input.length()*10000)/100.0+"%"
			+"\nOriginal Length (excluding space): "+ input.replaceAll(" ","").length()
			+"\nOriginal Length (including space): "+ input.length()
			+"\nCompressed Length: "+results.length()+"\n");

		}
		else {
			results = translator.decode(input);}
		
		
		String invalidWords = translator.getInvalidWords();
		
		if(errorCheckingPost(translator.getInvalidWords())) {
			outputResults.setText(results);};
		}
		
		}

	
	
	private void initLabels_Text_TextFields() {
		evalLabel = new Label("JonesSpeak: Dictionary Compression");
		evalLabel.setFont(Font.font("Cambria Math",20));
	    outputResults = new Text(0,0,"");
	    outputResults.setFont(Font.font("Consolas",18));
		expression = new TextField();
		expression.setPrefWidth(600);
		expression.setFont(Font.font("Consolas",18));	
		
		stats = new Text("");
		stats.setFont(Font.font("Cambria Math",10));
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
		evalResults.add(stats, 0, 4);
		
		evalResults.setHgap(10); 
		evalResults.setVgap(10); 
		evalResults.setPadding(new Insets(10, 10, 10, 10)); 
		GridPane.setHalignment(evalLabel, HPos.CENTER);
		GridPane.setHalignment(outputResults, HPos.CENTER);		
	}
	
	private void createTree() throws IOException {
		outputResults.setText("Building Huffman Tree...");
		translator = new Huffman();
		outputResults.setText("");

		
	}
	
	private boolean errorCheckingPre(String input) {
				
		if(input.equals("")) {
			alert("Please fill the text field");
			return false;}
		return true;
	}
	
	private boolean errorCheckingPost(String invalidWords) {
		if(invalidWords.equals("")) {
			return true;}
		else {
			if (invalidWords.replaceAll(",","").equals(invalidWords)){
					alert("\""+invalidWords+"\"  is not in jonesspeak");}
			else {
				alert("The following words are not in jonesspeak: "+invalidWords);}
		}	
		return false;
	}
	
	
	public static void main(String[] args) throws IOException {
		Application.launch(args);

	}
	
	public void start(Stage primaryStage) throws Exception {
		initLabels_Text_TextFields();
		initButtons_HBox();
		initGridPane();	
		evalPane = new BorderPane();		
		evalPane.setCenter(evalResults);
		evalPane.setBackground(new Background(new BackgroundFill(Color.web("eeeeee"), null, null)));
		Scene scene = new Scene(evalPane);
		primaryStage.setTitle("JonesSpeak v" + version);
		primaryStage.setScene(scene);
		primaryStage.show();
		createTree();

	}

	private void alert(String errorMsg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Invalid Input");
		alert.setContentText(errorMsg);
		alert.showAndWait();			
		}
	
	
	}

	


