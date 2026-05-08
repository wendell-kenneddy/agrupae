import { useNavigate } from 'react-router-dom'

export function LandingPage() {
  const navigate = useNavigate()

  return (
    <main>
      <img src="logo-completa.svg" alt="Agrupaê"></img>

      <h1>
        Onde o trabalho em grupo <strong>finalmente funciona.</strong>
      </h1>
      <p>Gerencie pessoas, arquivos e prazos com a organização que o seu semestre exige.</p>

      <button onClick={() => navigate('/register')}>Fazer cadastro</button>
      <button onClick={() => navigate('/login')}>Entrar</button>
    </main>
  )
}
