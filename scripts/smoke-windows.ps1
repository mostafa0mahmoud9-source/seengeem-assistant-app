$ErrorActionPreference = 'Stop'
$setup = Get-ChildItem dist\*.exe | Select-Object -First 1
if (!$setup) { throw 'Missing Setup executable' }
$destination = Join-Path $env:RUNNER_TEMP 'sgh-install-test'
$installer = Start-Process -FilePath $setup.FullName -ArgumentList @('/S', "/D=$destination") -Wait -PassThru
if ($installer.ExitCode -ne 0) { throw "Setup failed: $($installer.ExitCode)" }
$app = Get-ChildItem $destination -Filter 'SeenGeem Assistant.exe' -Recurse | Select-Object -First 1
if (!$app) { throw 'Installed executable not found' }
$process = Start-Process -FilePath $app.FullName -PassThru
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
  Start-Sleep -Seconds 1
  $process.Refresh()
  if ($process.HasExited) { throw "App exited before its window opened: $($process.ExitCode)" }
  if ($process.MainWindowHandle -ne 0) { $ready = $true; break }
}
if (!$ready) { Stop-Process -Id $process.Id -Force; throw 'Installed application window did not open' }
Write-Host 'Setup installed successfully and the installed application opened a native window.'
$process.CloseMainWindow() | Out-Null
if (!$process.WaitForExit(10000)) { Stop-Process -Id $process.Id -Force }
