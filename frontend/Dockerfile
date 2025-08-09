# Stage 1: Build Angular app
FROM node:18.20.5 AS build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy source files
COPY . .

# Build Angular app
RUN npm run build

# Stage 2: Serve Angular app with Nginx
FROM nginx:stable-alpine

# Copy built app from previous stage
COPY --from=build /app/target/ /usr/share/nginx/html/

# Optional: replace default nginx config if needed
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
