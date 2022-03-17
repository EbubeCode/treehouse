module.exports = {
  mode: process.env.NODE_ENV ? 'jit' : undefined,
  content: ['./src/**/*.{html,js}'],
  theme: {
    extend: {},
  },
  plugins: [],
}
