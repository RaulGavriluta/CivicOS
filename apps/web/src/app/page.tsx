'use client';

import { useEffect, useState } from 'react';
import { fetchApi } from '@/lib/api-client';

interface HealthResponse {
  status: string;
}

export default function Home() {
  const [status, setStatus] = useState<string>('CHECKING...');
  const [isUp, setIsUp] = useState<boolean>(false);

  useEffect(() => {
    fetchApi<HealthResponse>('/health')
      .then((data) => {
        if (data && data.status === 'UP') {
          setStatus('UP');
          setIsUp(true);
        } else {
          setStatus('DOWN');
          setIsUp(false);
        }
      })
      .catch(() => {
        setStatus('OFFLINE (Could not reach backend)');
        setIsUp(false);
      });
  }, []);

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24 bg-black text-white">
      <h1 className="text-4xl font-bold mb-8">CivicOS Platform</h1>
      <div className="text-xl">
        Backend Connection:{' '}
        <span className={isUp ? 'text-green-500 font-semibold' : 'text-red-500 font-semibold'}>
          {status}
        </span>
      </div>
    </main>
  );
}
