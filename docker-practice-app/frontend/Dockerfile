FROM node:22-slim AS builder

WORKDIR /src

COPY package.json package-lock.json* ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .
RUN npm run build

FROM nginx:1.27-alpine AS runner

WORKDIR /app

COPY --chown=nginx:nginx --from=builder /src/dist /usr/share/nginx/html

EXPOSE 8080

ENTRYPOINT ["nginx", "-c", "/etc/nginx/nginx.conf"]
CMD ["-g", "daemon off;"]