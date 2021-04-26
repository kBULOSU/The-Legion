import br.com.idea.api.shared.ApiConstants;

public class tyest {

    public static void main(String[] args) {
        String[] strings = new String[]{
                "a",
                "b",
                "c"
        };

        String a = ApiConstants.GSON.toJson(strings);

        System.out.println(a);
    }

}
