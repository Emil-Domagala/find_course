export type ApiErrorResponse = {
  status: number;
  data: {
    status: number;
    message: string;
    errors?: [
      {
        field: string;
        message: string;
      },
    ];
  };
};
