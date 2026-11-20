# Sistema de Pase de Lista (UNAM FES Aragón - Ingeniería en Computación)

Aplicación web MVC desarrollada en **Java 17 + Spring Boot + Thymeleaf + Spring Security + H2 Database**.

## Requisitos previos
- **Java JDK 17+** (`java -version`)
- **Apache Maven 3.8+** (`mvn -version`)

### Instalación rápida en macOS (Homebrew)
```bash
brew install openjdk@17 maven
```

## Cómo ejecutar el proyecto
1. Descomprime `pase-lista-app.zip`.
2. Abre una terminal dentro de la carpeta `pase-lista-app/`.
3. Ejecuta:
```bash
mvn spring-boot:run
```
4. Abre tu navegador en: `http://localhost:8080/login`

## Flujo rápido de prueba
1. Haz clic en **Regístrate aquí** y crea una cuenta de profesor.
2. Inicia sesión y crea una materia en **+ Nueva Materia** (para probar el pase de lista hoy mismo, marca el día actual y un horario que incluya tu hora actual).
3. Entra a **Alumnos** y carga el archivo incluido `alumnos_ejemplo.csv` o agrega alumnos manualmente (número de cuenta de 9 dígitos).
4. Entra a **Pasar Lista**, marca presentes/ausentes y guarda.
5. Consulta el acumulado y porcentajes en **Reporte**.

## Consola de Base de Datos H2
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/paselista`
- User Name: `sa`
- Password: *(vacío)*
