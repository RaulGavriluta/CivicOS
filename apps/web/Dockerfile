FROM oven/bun:1-alpine

WORKDIR /app

ENV PATH="/app/apps/web/node_modules/.bin:/app/node_modules/.bin:$PATH"
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

COPY package.json bun.lock ./
COPY apps/web/package.json ./apps/web/package.json

RUN bun install --frozen-lockfile

COPY apps/web ./apps/web

WORKDIR /app/apps/web

EXPOSE 3000

CMD ["bun", "run", "dev"]