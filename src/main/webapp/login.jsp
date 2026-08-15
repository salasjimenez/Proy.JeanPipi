<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JeanPipi | Acceso</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container d-flex justify-content-center align-items-center vh-100">
        <div class="card p-4 shadow-sm" style="width: 400px; max-width: 100%;">
            <h3 class="text-center fw-bold mb-4">JEANPIPI</h3>
            <form action="api/auth" method="POST">
                <div class="mb-3">
                    <label class="form-label">Correo Electrónico</label>
                    <input type="email" name="email" class="form-control" autocomplete="username" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" name="contrasena" class="form-control" autocomplete="current-password" required>
                </div>
                <button type="submit" class="btn btn-dark w-100">Ingresar</button>
            </form>
        </div>
    </div>
    <script src="js/api.js"></script>
    <script src="js/login.js"></script>
</body>
</html>
