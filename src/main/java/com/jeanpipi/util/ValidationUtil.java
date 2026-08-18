package com.jeanpipi.util;

// Validaciones de entrada.
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private ValidationUtil() {}

    public static List<String> registration(String name, String email, String password) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().length() < 2 || name.trim().length() > 120) {
            errors.add("El nombre debe tener entre 2 y 120 caracteres");
        }
        if (email == null || email.length() > 190 || !EMAIL.matcher(email.trim()).matches()) {
            errors.add("El correo no es valido");
        }
        if (password == null || password.length() < 10 || password.length() > 128) {
            errors.add("La contrasena debe tener entre 10 y 128 caracteres");
        } else if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*")) {
            errors.add("La contrasena debe incluir mayuscula, minuscula y numero");
        }
        return errors;
    }

    public static List<String> article(String title, String content) {
        List<String> errors = new ArrayList<>();
        if (title == null || title.trim().length() < 5 || title.trim().length() > 220) {
            errors.add("El titulo debe tener entre 5 y 220 caracteres");
        }
        if (content == null || HtmlUtil.plainText(content).trim().length() < 20) {
            errors.add("El contenido es demasiado corto");
        }
        return errors;
    }

    public static boolean validEmail(String email) {
        return email != null && email.length() <= 190 && EMAIL.matcher(email.trim()).matches();
    }
}
