import lombok.*;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Leaf extends Node{
	
	private final String word;
	
	public Leaf(String word, int frequency) {
		super(frequency);
		this.word = word;
	}
	

	

}
