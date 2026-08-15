<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JeanPipi | Panel Administrativo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container my-5">
        <div class="d-flex justify-content-between align-items-center mb-4" style="gap: 1rem; flex-wrap: wrap;">
            <h2>Administración - JeanPipi Magazine</h2>
            <a href="index.jsp" class="btn btn-outline-secondary">← Volver al Inicio</a>
        </div>
        <div class="card p-4 mb-4 shadow-sm">
            <h4>Crear Nuevo Artículo</h4>
            <form id="form-crear-articulo">
                <input type="hidden" name="categoriaId" value="1">
                <div class="mb-3">
                    <label class="form-label">Título</label>
                    <input type="text" name="titulo" class="form-control" maxlength="200" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Descripción</label>
                    <input type="text" name="descripcion" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">URL Imagen</label>
                    <input type="url" name="imagen" class="form-control" placeholder="https://ejemplo.com/imagen.jpg">
                </div>
                <div class="mb-3">
                    <label class="form-label">Contenido</label>
                    <textarea name="contenido" class="form-control" rows="5" required></textarea>
                </div>
                <button type="submit" class="btn btn-dark">Publicar Artículo</button>
            </form>
        </div>
    </div>
    <script src="js/api.js"></script>
    <script src="js/admin.js"></script>
</body>
</html>
