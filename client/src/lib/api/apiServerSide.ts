import { ApiErrorResponse } from '@/types/apiError';
import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';


class ApiService {
  private api: AxiosInstance;
  private static instance: ApiService;

  private constructor() {
    this.api = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
      withCredentials: true,
      
    });
  }

  public static getInstance(): ApiService {
    if (!ApiService.instance) {
      ApiService.instance = new ApiService();
    }
    return ApiService.instance;
  }

  // private handleError(error: AxiosError): ApiErrorResponse {
  //   console.log('Handling axios error');
  //   if (error.response?.data) return error.response.data as ApiErrorResponse;

  //   return {
  //     status: error.response?.status || 500,
  //     data: { status: error.response?.status || 500, message: 'Something went wrong on the server-side API call' },
  //   };
  // }

  // private async refreshToken(): Promise<void> {
  //   try {
  //     console.log('Token refresh call function');
  //     await this.api.post('/public/refresh-token', {});
  //   } catch (refreshError) {
  //     console.error('Server-side refresh token call failed:', refreshError);
  //   }
  // }

  // private async logout() {
  //   try {
  //     await this.api.post('/public/logout');
  //   } catch (logoutError) {
  //     console.error('Server-side logout call failed:', logoutError);
  //   }
  // }

  public async getCoursesPublic(courseId: string): Promise<CourseDetailsPublicDto> {
    const response: AxiosResponse<CourseDetailsPublicDto> = await this.api.get(`/public/courses/${courseId}`);
    return response.data;
  }

  // public async getUserInfo(): Promise<UserDto> {
  //   const response: AxiosResponse<UserDto> = await this.api.get(`/user`);
  //   return response.data;
  // }

  public getAxiosInstance(): AxiosInstance {
    return this.api;
  }
}

export const apiServerService = ApiService.getInstance();

