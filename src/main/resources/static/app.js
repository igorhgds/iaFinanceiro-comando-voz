const textoDiv = document.getElementById('texto');
const respostaDiv = document.getElementById('resposta');
document.getElementById("button-voice").addEventListener("click", escutarConsulta);

function escutarConsulta() {
    document.getElementById("button-icon").classList.remove("fa-volume-xmark");
    document.getElementById("button-icon").classList.add("fa-volume-high");

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
                perguntarSeQuerContinuar();
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

function perguntarSeQuerContinuar() {
    const seguir = new SpeechSynthesisUtterance("Deseja mais alguma coisa?");
    seguir.lang = 'pt-BR';

    seguir.onend = () => {
        const recognitionFollowUp = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
        recognitionFollowUp.lang = 'pt-BR';
        recognitionFollowUp.continuous = false;
        recognitionFollowUp.interimResults = false;

        recognitionFollowUp.onresult = (event) => {
            const respostaUsuario = event.results[0][0].transcript.toLowerCase().trim();
            console.log("Resposta ao continuar:", respostaUsuario);
            recognitionFollowUp.stop();

            if (respostaUsuario.includes("não") || respostaUsuario.includes("nao")) {
                respostaDiv.textContent = "Ok, até a próxima!";
                const despedida = new SpeechSynthesisUtterance("Ok, até a próxima!");
                despedida.lang = 'pt-BR';
                despedida.onend = () => {
                    document.getElementById("button-icon").classList.remove("fa-volume-high");
                    document.getElementById("button-icon").classList.add("fa-volume-xmark");
                    // Aqui finaliza de fato
                };
                speechSynthesis.speak(despedida);
            } else {
                escutarConsulta();
            }
        };

        recognitionFollowUp.onerror = function (event) {
            respostaDiv.textContent = "Erro de voz ao continuar: " + event.error;
        };

        recognitionFollowUp.start();
    };

    speechSynthesis.speak(seguir);
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

