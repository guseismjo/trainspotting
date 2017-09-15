import java.util.*;
public class Tuple<a> {
	public a x;
	public a y;
	private a[] contents;

	public Tuple(a x, a y){
		this.x = x;
		this.y = y;
        contents = (a[])new Object[2];
        contents[0] = this.x;
        contents[1] = this.y;

	}

    @Override
    public int hashCode () {

        return Arrays.deepHashCode(this.contents);
    }


}
