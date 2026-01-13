# Script para probar endpoints de presupuesto
# Ejecutar desde PowerShell en el directorio del proyecto

Write-Host "🔍 INICIANDO PRUEBAS DE ENDPOINTS DE PRESUPUESTO" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Función para hacer peticiones HTTP
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = "",
        [string]$Description
    )
    
    Write-Host "`n🧪 Probando: $Description" -ForegroundColor Yellow
    Write-Host "URL: $Method $Url" -ForegroundColor Cyan
    
    try {
        if ($Body -ne "") {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Body $Body -ContentType "application/json"
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method
        }
        
        Write-Host "✅ ÉXITO - Status: 200" -ForegroundColor Green
        Write-Host "Respuesta: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor White
        return $true
    }
    catch {
        Write-Host "❌ ERROR - Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        Write-Host "Mensaje: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Base URL
$baseUrl = "http://localhost:8080"

# Prueba 1: Test del controlador
Test-Endpoint -Method "GET" -Url "$baseUrl/api/budgets/test" -Description "Test del controlador de presupuestos"

# Prueba 2: Endpoint de debug simple
$simpleData = '{"test": "data"}'
Test-Endpoint -Method "POST" -Url "$baseUrl/api/budgets/test-simple" -Body $simpleData -Description "Endpoint de debug simple"

# Prueba 3: Endpoint de debug simple con datos vacíos
$emptyData = '{}'
Test-Endpoint -Method "POST" -Url "$baseUrl/api/budgets/test-simple" -Body $emptyData -Description "Endpoint de debug simple con datos vacíos"

# Prueba 4: Endpoint de debug Map
$mapData = '{
  "title": "Test Project",
  "serviceType": "Web Development",
  "description": "Test Description",
  "budget": 5000.0,
  "timeline": "3 months"
}'
Test-Endpoint -Method "POST" -Url "$baseUrl/api/budgets/test-map" -Body $mapData -Description "Endpoint de debug Map"

# Prueba 5: Endpoint de debug original
$debugData = '{
  "title": "Test Project",
  "serviceType": "Web Development"
}'
Test-Endpoint -Method "POST" -Url "$baseUrl/api/budgets/debug" -Body $debugData -Description "Endpoint de debug original"

# Prueba 6: Verificar si existe cliente con ID 1
Test-Endpoint -Method "GET" -Url "$baseUrl/api/clients/1" -Description "Verificar cliente con ID 1"

# Prueba 7: Crear presupuesto para cliente 1
$budgetData = '{
  "title": "Test Project",
  "serviceType": "Web Development",
  "description": "Test Description",
  "budget": 5000.0,
  "timeline": "3 months"
}'
Test-Endpoint -Method "POST" -Url "$baseUrl/api/budgets/client/1" -Body $budgetData -Description "Crear presupuesto para cliente 1"

Write-Host "`n🎯 PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "====================" -ForegroundColor Green
Write-Host "Revisa los logs del backend para ver los detalles de cada petición." -ForegroundColor Yellow
Write-Host "Si alguna prueba falla, el problema está identificado." -ForegroundColor Yellow
