// Manual mock for axios — keeps tests offline and free
const axios = {
  post: jest.fn(),
  get: jest.fn(),
  isAxiosError: (err: unknown): boolean =>
    typeof err === "object" && err !== null && "__isAxios" in err,
};

export default axios;
