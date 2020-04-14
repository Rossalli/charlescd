export enum METRICS_TYPE {
  REQUESTS_BY_CIRCLE = 'REQUESTS_BY_CIRCLE',
  REQUESTS_ERRORS_BY_CIRCLE = 'REQUESTS_ERRORS_BY_CIRCLE',
  REQUESTS_LATENCY_BY_CIRCLE = 'REQUESTS_LATENCY_BY_CIRCLE'
}

export enum METRICS_SPEED {
  SLOW_TIME = 300000,
  FAST_TIME = 10000
}

export enum CHART_TYPE {
  COMPARISON = 'COMPARISON',
  NORMAL = 'NORMAL'
}

export enum PROJECTION_TYPE {
  FIVE_MINUTES = 'FIVE_MINUTES',
  THIRTY_MINUTES = 'THIRTY_MINUTES',
  ONE_HOUR = 'ONE_HOUR',
  THREE_HOUR = 'THREE_HOUR',
  EIGHT_HOUR = 'EIGHT_HOUR'
}
