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

  public async getCoursesPublic(courseId: string): Promise<CourseDetailsPublicDto> {
    const response: AxiosResponse<CourseDetailsPublicDto> = await this.api.get(`/public/courses/${courseId}`);
    return response.data;
  }

  public getAxiosInstance(): AxiosInstance {
    return this.api;
  }
}

export const apiServerService = ApiService.getInstance();

