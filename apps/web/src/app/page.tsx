import { fetchApi } from '@/lib/api-client';

interface HealthResponse {
  status: string;
}

export default async function HomePage() {
  let backendStatus = 'OFFLINE';
  let isError = false;

  try {
    const data = await fetchApi<HealthResponse>('/health', {
      cache: 'no-store',
    });
    backendStatus = data.status;
  } catch {
    backendStatus = 'OFFLINE (Could not reach backend)';
    isError = true;
  }

  return (
    <main style={{ padding: '3rem', fontFamily: 'system-ui, sans-serif' }}>
      <h1>CivicOS Platform</h1>
      <p style={{ marginTop: '1rem', fontSize: '1.1rem' }}>
        Backend Connection:{' '}
        <span
          style={{
            fontWeight: 'bold',
            color: isError ? '#dc2626' : '#16a34a',
          }}
        >
          {backendStatus}
        </span>
      </p>
    </main>
  );
}
