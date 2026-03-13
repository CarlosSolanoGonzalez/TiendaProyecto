document.addEventListener("DOMContentLoaded", () => {
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach((alert) => {
        window.setTimeout(() => {
            alert.classList.add("fade");
            alert.classList.add("show");
        }, 3500);
    });

    const imagenFile = document.getElementById("imagenFile");
    const previewImagen = document.getElementById("previewImagen");
    const previewContainer = document.getElementById("previewContainer");

    if (imagenFile && previewImagen && previewContainer) {
        imagenFile.addEventListener("change", (event) => {
            const [archivo] = event.target.files;
            if (!archivo) {
                previewImagen.removeAttribute("src");
                previewContainer.classList.add("d-none");
                return;
            }
            previewImagen.src = URL.createObjectURL(archivo);
            previewContainer.classList.remove("d-none");
        });
    }
});
