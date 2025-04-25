
const textoDiv = document.getElementById('texto');
const respostaDiv = document.getElementById('resposta');

function escutarConsulta() {
    const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
    recognition.lang = 'pt-BR';
    recognition.continuous = false;
    recognition.interimResults = false;

    recognition.onresult = async function (event) {
        const resultado = event.results[0][0].transcript.toLowerCase();
        textoDiv.textContent = "Você disse: " + resultado;
        console.log(resultado);
        recognition.stop();

        try {
            const response = await fetch("/consulta?texto=" + encodeURIComponent(resultado));
            const respostaTexto = await response.text();
            respostaDiv.textContent = "Resposta: " + respostaTexto;

            const utterance = new SpeechSynthesisUtterance(respostaTexto);
            utterance.lang = 'pt-BR';
            utterance.onend = () => {
                const seguir = new SpeechSynthesisUtterance("Deseja mais alguma coisa?");
                seguir.lang = 'pt-BR';
                seguir.onend = () => {
                    escutarConsulta(); // escuta novamente
                };
                speechSynthesis.speak(seguir);
            };

            speechSynthesis.speak(utterance);
        } catch (error) {
            respostaDiv.textContent = "Erro ao consultar o assistente.";
            console.error(error);
        }
    };

    recognition.onerror = function (event) {
        respostaDiv.textContent = "Erro de voz: " + event.error;
    };

    recognition.start();
}

document.addEventListener("keydown", function (e) {
    if (e.code === "Space") {
        respostaDiv.textContent = "Faça sua consulta.";
        const utterance = new SpeechSynthesisUtterance("Faça sua consulta.");
        utterance.lang = 'pt-BR';

        utterance.onend = () => {
            escutarConsulta();
        };

        speechSynthesis.speak(utterance);
    }
});
