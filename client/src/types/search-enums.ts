export enum SearchDirection {
  ASC = 'ASC',
  DESC = 'DESC',
}

export enum BecomeTeacherRequestStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  DENIED = 'DENIED',
}

export enum ChapterType {
  VIDEO = 'VIDEO',
  TEXT = 'TEXT',
}

export enum CourseDtoSortField {
  CreatedAt = 'createdAt',
  UpdatedAt = 'updatedAt',
  Title = 'title',
  Price = 'price',
};

export enum TeacherApplicationSortField {
  CreatedAt = 'createdAt',
  Status = 'status',
  SeenByAdmin = 'seenByAdmin',
};

export enum TransactionDtoSortField {
  CreatedAt = 'createdAt',
  Amount = 'amount',
};